; TODO address incorrect time offset generated via impetus

(ns tides.core
  (:require [reagent.core      :as    rc]
            [jayq.core         :refer [$ height width]]
            [jayq.util         :refer [log]]
            [tides.clientviews :as    views]
            [tides.util        :as    util]
            [cljd3.chart       :as    chart]
            [cljd3.core        :as    core])
  (:require-macros [jayq.macros :refer [ready let-ajax]]))

(def stock-state (rc/atom []))

; data is being returned as string
; work in a way to parse the date
; then use the date..
; maybe add another key to options
; once date object is available, add back :x-time-format "%m %d %Y"
; working following feature :x-time-format-str "%m %d %Y"
; it is unbelievable how shitty the interface is...
; unable to get the date formatting working - which should be a piece of cake.
(defn draw-linechart [stocks]
  (-> ($ "#linechart") (height 900) (width 900))
  (chart/line {:container "#linechart"
               :series    (vec stocks)
               :width     900
               :height    900
               :x         {:type :date
                           :format "%m-%d-%Y"
                           :ticks 10
                           :label "Date (MM-dd-yyyy)"}
               :y         {:label "SMA (price/MA(20))"
                           :ticks 10}}))

; looks like we have to always either use only attributes map in tags or not at all
; otherwise hiccup impls dont quite agree on what data should be used
(defn watchlist []
  "generate the watch list of all the stocks"
  (let [stocks @stock-state]
    (views/table 
      ["SYMBOL", "CLOSE/SMA(20)"]
      (for [stock stocks
            :let  [symbol (:symbol stock)
                   val    (-> (:values stock) last :y)]]       
        [:tr 
         [:td symbol]
         [:td val]])
      :id    "watchlist"
      :class "table")))

(defn div-svg []
  "simple stub component for the linechart to render intos"
  [:div {:id "linechart"}])

(defn linechart []
  "the linechart reagent component"
  (let [stocks  @stock-state
        fx      #(draw-linechart stocks)
        hooks   {:component-did-mount  fx
                 :component-did-update fx}
        meta    (with-meta div-svg hooks)]
    [meta]))

(defn render [json]
  "handle post processing of the data"
  (let [post    (util/post-process-data json)
        sample  (-> (first post) :values)
        updater #(identity {:x (:x %) :y 1})
        vals    (mapv updater sample)
        guide   {:key    "Guide"
                 :symbol "-ignore-" 
                 :values vals}
        added   (conj post guide)]
    added))

(defn select-first [selector]
  "little dom selecting hack for rendering reagent components"
  (-> (core/select selector) first first))

(ready
    (rc/render-component [watchlist] (select-first "#watchlist"))
    (rc/render-component [linechart] (select-first "#linechartcontainer"))
    (util/load-data "/impetus" #(reset! stock-state (render %))))

; uncomment below to use the test data instead
(comment 
  (ready
    (core/put-by-id (core/select "#linechartcontainer") "div" "linechart")
    (util/load-data "/impetus-test" #(draw-linechart (util/post-process-data %))))) 
