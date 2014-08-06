(ns tides.views
  (:require [tides.components :refer [view grid table let-map]]))

;------------------------------------------------------------
; TESTING
;------------------------------------------------------------
(def food
  [{:name "Pizza" :cost "low"}
   {:name "Chinese" :cost "high"}])

(def stocks
  [{:id "AAPL" :name "Apple Inc." :price 523.24}
   {:id "GOOG" :name "Google Inc." :price 748.38}])

;------------------------------------------------------------
; Views
;------------------------------------------------------------
(defn file-not-found
  []
  (view "File not found."))

(defn single-div
  "Load a simple empty page which will be populated dynamically
  (single-div :div#message)"
  [el]
  (view [el]))

; used to use this for headers
; for now unnecessary
(comment
  (grid
      [2 5]
      ["Symbols" "Line"]))

(defn index
  "Start the index page.
   Three widgets are displayed.
   Left bar is the list of symbols and their current prices.
   Mid chart is the multi-line chart,
   Right chart is the stacked bar chart."
  []
  (view
    (grid
      [:div.col-xs-2 :div.col-xs-10]
      [(table 
         ["Symbol", "Price"]
         (let-map 
           [s stocks]
           [:tr
            [:td (:id s)]
            [:td (:price s)]])
     :id "watchlist"
     :class "table")
       [:div#linechartcontainer nil]])))