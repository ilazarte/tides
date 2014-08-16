; TODO address incorrect time offset generated via impetus

(ns tides.core
  (:require [reagent.core      :as    rc]
            [jayq.core         :refer [$ height width]]
            [jayq.util         :refer [log]]
            [tides.clientviews :as    views]
            [tides.util        :as    util]
            [arbol.core        :as    arbol]
            [cljd3.chart       :as    chart]
            [cljd3.core        :as    core])
  
  (:require-macros [jayq.macros :refer [ready let-ajax]]))

(def stock-state (rc/atom []))

(def watchlist-state (rc/atom []))

(defn- rename
  "Rename a map key"
  [m frm to]
  (-> 
    (assoc m to (frm m))
    (dissoc frm)))

(defn- make-guide
  "create a dataset to be used for 1.0 guide line.
   this is a hack and a real graphing lib would allow
   for a simple line to be created in the graph space."
  [data val]
  (let [sample  (-> (first data) :values)
        updater #(identity {:x (:x %) :y val})
        vals    (mapv updater sample)]
    {:key    "Guide"
     :symbol "-ignore-" 
     :values vals}))

(defn load-initial! [json]
  "handle post processing of the data
   set the stock and mouse over bar data"
  (let [data      (util/post-process-data json)
        wldata    (arbol/climb
                    data
                    [:.map #(contains? % :symbol)]
                    #(merge % (-> (:values %) last))
                    #(dissoc % :values :symbol)
                    [] vec)
        graphdata (conj data (make-guide data 1))]
    (reset! watchlist-state wldata)
    (reset! stock-state graphdata)
    nil))

(defn draw-linechart [stocks]
  (-> ($ "#linechart") (height 900) (width 900))
  (chart/line {:container "#linechart"
               :series    (vec stocks)
               :width     900
               :height    900
               :x         {:type      :date
                           :format    "%m-%d-%Y"
                           :ticks     10
                           :label     "Date (MM-dd-yyyy)"
                           :mouseover #(reset! watchlist-state %)}
               :y         {:label "PMAr(20)"
                           :ticks 10}}))

(defn div-svg []
  "simple stub component for the linechart to render intos"
  [:div {:id "linechart"}])

(defn select-first [selector]
  "little dom selecting hack for rendering reagent components"
  (-> (core/select selector) first first))

; ------------------------------
; components
; ------------------------------

; looks like we have to always either use only attributes map in tags or not at all
; otherwise hiccup impls dont quite agree on what data should be used.
(defn watchlist []
  "generate the watch list of all the stocks"
  (let [stocks   @watchlist-state
        sorted   (-> (sort-by :y stocks) reverse)
        fpred    #(not= "Guide" (:key %))
        filtered (filter fpred sorted)]
    (views/table 
      ["Symbol", "PMAr(20)"]
      (for [stock filtered
            :let  [key (:key stock)
                   val (:y stock)]]       
        [:tr 
         [:td key]
         [:td val]])
      :id    "watchlist"
      :class "table")))

(defn linechart []
  "the linechart reagent component"
  (let [stocks  @stock-state
        fx      #(draw-linechart stocks)
        hooks   {:component-did-mount  fx
                 :component-did-update fx}
        meta    (with-meta div-svg hooks)]
    [meta]))

(ready
  (rc/render-component [watchlist] (select-first "#watchlist"))
  (rc/render-component [linechart] (select-first "#linechartcontainer"))
  (util/load-data "/impetus" #(load-initial! %)))

; uncomment below to use the test data instead
(comment 
  (ready
    (core/put-by-id (core/select "#linechartcontainer") "div" "linechart")
    (util/load-data "/impetus-test" #(draw-linechart (util/post-process-data %))))) 
