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
(def stock-state-intraday (rc/atom []))

(def watchlist-state (rc/atom []))
(def watchlist-state-intraday (rc/atom []))

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

(defn load-initial! [json daily]
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
    (reset! (if daily watchlist-state watchlist-state-intraday) wldata)
    (reset! (if daily stock-state stock-state-intraday) graphdata)
    nil))

(defn draw-linechart [stocks daily]
  (chart/line {:container (if daily "#linechart" "#linechart-intraday")
               :series    (vec stocks)
               :height    600
               :width     900
               :x         {:type      :date
                           :format    (if daily "%m-%d-%Y" "%I:%M")
                           :ticks     (if daily 5 10)
                           :label     "Date"
                           :mouseover #(reset! (if daily watchlist-state watchlist-state-intraday) %)}
               :y         {:label "PMAr(20)"
                           :ticks 10}}))

(defn div-svg []
  "simple stub component for the linechart to render intos"
  [:div {:id "linechart"}])

(defn div-svg-intraday []
  "simple stub component for the linechart to render intos"
  [:div {:id "linechart-intraday"}])

(defn select-first [selector]
  "little dom selecting hack for rendering reagent components"
  (-> (core/select selector) first first))

; ------------------------------
; components
; ------------------------------

; NOTE reagent doesn't replace outermost tag (for example table)
; when it replaces it does the child of only...
; this means you can't replace the id/class on update, just inside.
; .. makes sense, probably used for events etc.
;
;
; looks like we have to always either use only attributes map in tags or not at all
; otherwise hiccup impls dont quite agree on what data should be used.
(defn watchlist [daily]
  "generate the watch list of all the stocks"
  (let [stocks   (if daily @watchlist-state @watchlist-state-intraday)
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
      :id    (if daily "watchlist" "watchlist-intraday")
      :class "table table-striped")))

(defn watchlist-daily []
  (watchlist true))

(defn watchlist-intraday []
  (watchlist false))

(defn linechart [daily]
  "the linechart reagent component"
  (let [stocks  (if daily @stock-state @stock-state-intraday)
        fx      #(draw-linechart stocks daily)
        hooks   {:component-did-mount  fx
                 :component-did-update fx}
        meta    (with-meta (if daily div-svg div-svg-intraday) hooks)]
    [meta]))

(defn linechart-daily []
  (linechart true))

(defn linechart-intraday []
  (linechart false))

(if (= document.location.pathname "/relative")
  (ready
    (rc/render-component [watchlist-daily] (select-first "#watchlist"))
    (rc/render-component [linechart-daily] (select-first "#linechartcontainer"))
    (rc/render-component [watchlist-intraday] (select-first "#watchlist-intraday"))
    (rc/render-component [linechart-intraday] (select-first "#linechartcontainer-intraday"))
    (util/load-data "/impetus" #(load-initial! % true))
    (util/load-data "/impetus-intraday" #(load-initial! % false))))



; uncomment below to use the test data instead
(comment 
  (ready
    (core/put-by-id (core/select "#linechartcontainer") "div" "linechart")
    (util/load-data "/impetus-test" #(draw-linechart (util/post-process-data %))))) 
