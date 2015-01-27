(ns tides.svgchart
  (:require [reagent.core      :as    rc]
            [jayq.core         :refer [$ height width]]
            [jayq.util         :refer [log]]
            [tides.clientviews :as    views]
            [tides.util        :as    util]
            [arbol.core        :as    arbol]
            [cljd3.chart       :as    chart]
            [cljd3.core        :as    core])
  
  (:require-macros 
    [jayq.macros  :refer [ready let-ajax]]
    [tides.macros :refer [foreach]]))

(def stock-state (rc/atom []))

; TODO a real api wouldn't need this
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
    (js/console.log "loaded initial")
    (js/console.log (clj->js graphdata))
    (reset! stock-state graphdata)
    nil))

(defn select-first [selector]
  "little dom selecting hack for rendering reagent components"
  (-> (core/select selector) first first))

; ------------------------------
; components
; ------------------------------

; TODO let map works, but isn't there a better way to iterate?
; TODO it would be nice to have a scale inserted here instead
; TODO scale vs value?
; TODO how would you use a property in the stocks object as a value?
; ANNOYING! 
; 1. components must have a root node
; 2. for reagent to render correctly should always str

; lEARNED:
; hiccup sequences are expanded like hiccup

; very specific to the ChartData values
; get the dimension (x/y/etc) from values
; do functions get hoisted?
(defn domain [chartdata dim]
  (let [getter #(map dim (:values %))
        vals   (if (nil? chartdata) [] (mapcat getter chartdata))]
    (when-not (empty? vals) 
      [(reduce min vals) (reduce max vals)])))

(defn stock-text [attrs stocks]
  [:g attrs
   (foreach [stock stocks
             i     (range (count stocks))]
     (let [y (* i 10)]
       [:text {:transform (str "translate(0, " y ")")} (str y " !=> " (:key stock))]))])

; http://stackoverflow.com/questions/5294955/how-to-scale-down-a-range-of-numbers-with-a-known-min-and-max-value
; return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
(defn linear-scale [from to]
  (let [bmin (first from)
        bmax (second from)
        lmin (first to)
        lmax (second to)
        ldiff (- lmax lmin)
        bdiff (- bmax bmin)]
    (fn [val]
      (let [vdiff  (- val bmin)
            top    (* ldiff vdiff)
            ranged (/ top bdiff)
            res    (+ ranged lmin)]
        res))))

; TODO this is a place to use scales
; TODO consider the starting point in the domain as well (for example 20-100)
(defn axis [attrs config]
  (let [orient (get config :orient :horizontal)
        domain (get config :domain [0 1])
        steps  (get config :steps 5)
        max    (get config :max)
        xydom  [0 max]
        sdom   [0 steps]
        srange (range steps)
        stepscale (linear-scale sdom domain)
        xyscale   (linear-scale domain xydom)
        domvals   (map #(stepscale %) srange)
        xyvals    (map #(xyscale %) domvals)]
    (do
      (js/console.log "domvals, xyvals, orientation:")
      (js/console.log (clj->js orient))
      (js/console.log (clj->js domvals))
      (js/console.log (clj->js xyvals)))
    (cond
      (= orient :vertical)
      [:g attrs 
       (foreach [dval (reverse domvals)
                 yval xyvals]
         [:g {:transform (str "translate(0, " yval ")")}
            [:line {:x2 -6 :stroke-width "1" :stroke "black"}]
            [:text {:x -9 :style {:text-anchor "end"}} dval]]) 
       [:line {:y2 max :stroke-width "1" :stroke "black"}]]
      (= orient :horizontal)
      [:g attrs
       (foreach [dval domvals
                 xval xyvals]
         [:g {:transform (str "translate(" xval ", 0)")}
            [:line {:y2 6 :stroke-width "1" :stroke "black"}]
            [:text {:y 15 :style {:text-anchor "middle"}} dval]]) 
       [:line {:x2 max :stroke-width "1" :stroke "black"}]]
      :else 
      [:text (str "invalid orientation " (clj->js orient))])))

(defn line [data]
  )

(defn linechart-svg []
  "generate the watch list of all the stocks"
  (let [stocks @stock-state    
        y-domain (domain stocks :y)
        x-domain (domain stocks :x)]
    (do 
      (js/console.log "x,y domains:")
      (js/console.log (clj->js x-domain))
      (js/console.log (clj->js y-domain)))
    [:svg {:style {:width  900 
                   :height 600}}
     [:g {:transform "translate(50, 20)"}
      [axis 
       {:class "y-axis"} 
       {:max 550 :orient :vertical :domain y-domain}]
      [axis
       {:class "x-axis" :transform "translate(0, 550)"}
       {:max 890 :orient :horizontal :domain x-domain}]]]))

(ready
  (when (= js/document.location.pathname "/svgchart")
    (rc/render-component [linechart-svg] (select-first "#main"))  
    (util/load-data "/impetus" #(load-initial! %))))
 
