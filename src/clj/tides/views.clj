(ns tides.views
  (:require
    [hiccup.page :refer [html5 include-js include-css]]))

;------------------------------------------------------------
; TESTING
;------------------------------------------------------------
(def food
  [{:name "Pizza" :cost "low"}
   {:name "Chinese" :cost "high"}])

(def stocks
  [{:id "AAPL" :name "Apple Inc." :price 523.24}
   {:id "IBM" :name "IBM Inc." :price 12.24}
   {:id "GOOG" :name "Google Inc." :price 748.38}])

;------------------------------------------------------------
; TESTING
;------------------------------------------------------------

; use this to factor out extra templating somehow?
; => (grid [1 2 3] ["a" "b" "c"])
; ([:div.col-xs-1 "a"] [:div.col-xs-2 "b"] [:div.col-xs-3 "c"])

(defn grid [nums vals & attrs]
  (let [to-tag-kw #(-> (str "div.col-xs-" %) keyword)
        converter #(if (keyword? %) % (to-tag-kw %))
        mapper    #(vector (converter %1) %2)
        defattrs  {:class "row"}]
    [:div (merge defattrs (first attrs)) 
     (map mapper nums vals)]))

(defn include-all-js
  []
  (include-js 
    "/webjars/jquery/2.1.3/jquery.min.js"
    "/webjars/bootstrap/3.3.1/js/bootstrap.min.js"
    "/webjars/react/0.11.1/react.js"
    "/webjars/d3js/3.5.2/d3.js"
    "/js/goog/base.js" 
    "/js/tides.js"))

(defn include-all-css 
  []
  (include-css
    "/webjars/bootstrap/3.3.1/css/bootstrap.min.css" 
    "/webjars/bootstrap/3.3.1/css/bootstrap-theme.min.css"
    "/css/line-chart.css"))


(defmacro view
  "Abstract away the wrapping view stuff.
   Eventually be able to conditional include the browser repl script
   Eventually handle title?"
  [& forms]
  `(html5 
     [:head (include-all-css)]
     [:body
      ~@forms
      (include-all-js)
      [:script "goog.require('tides.core');"]]))

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

(defn home
  "Welcome page for the entire webapp"
  []
  (view
    "Placeholder"))

(defn relative
  "Start the index page.
   Three widgets are displayed.
   Left bar is the list of symbols and their current prices.
   Mid chart is the multi-line chart,
   Right chart is the stacked bar chart."
  []
  (view
    [:div.row {:style "height:600px;"}
     [:div.col-xs-2 {:style "overflow-x:scroll; overflow-y hidden; height: 100%;"}
      [:table#watchlist.table.table-striped nil]]
     [:div.col-xs-10
      [:div#linechartcontainer nil]]]
    [:div.row {:style "height:600px;"}
     [:div.col-xs-2 {:style "overflow-x:scroll; overflow-y hidden; height: 100%;"}
      [:table#watchlist-intraday.table.table-striped nil]]
     [:div.col-xs-10
      [:div#linechartcontainer-intraday nil]]]))