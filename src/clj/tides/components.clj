(ns tides.components
  (:require
    [hiccup.page :refer [html5 include-js include-css]]))

(defn include-all-css 
  []
  (include-css
    "/webjars/bootstrap/3.2.0/css/bootstrap.min.css" 
    "/webjars/bootstrap/3.2.0/css/bootstrap-theme.min.css"
    "/css/line-chart.css"))

(defn include-all-js
  []
  (include-js 
    "/webjars/jquery/2.1.1/jquery.min.js"
    "/webjars/bootstrap/3.2.0/js/bootstrap.min.js"
    "/webjars/react/0.11.1/react.js"
    "/webjars/d3js/3.4.11/d3.js"
    "/js/goog/base.js" 
    "/js/tides.js"))

(defmacro as-sequential
  [obj]
  `(if (sequential? ~obj) ~obj [~obj]))

; (html (tag-sequential food :tr #(tag-map % :td :name :cost)))
; <tr><td>Pizza</td><td>low</td></tr><tr><td>Chinese</td><td>high</td></tr>
; for most cases see let-map

; http://blog.jayfields.com/2010/07/clojure-destructuring.html

(defn tag-sequential
  "For each value in the val, wrap with a tagname.
   Val is converted to a sequential first"
  [val tagname & [accessor]]
  (map #(vector tagname (if accessor (accessor %) %)) (as-sequential val)))

(defn tag-map
  "Tag a map.  The tagname is repeated for each passed in key.
   Key may also be a function" 
  [mval tagname & keyfns]
  (map #(vector tagname %) (map #(% mval) keyfns)))

(defmacro let-map
  ";=> (let-map [i (range 5)] (inc i))"
  [decl form] 
  (let [bindings (take-nth 2 decl)
        colls    (take-nth 2 (rest decl))]
    `(map (fn [~@bindings] ~form) ~@colls)))

;------------------------------------------------------------
; Bootstrap components
; http://getbootstrap.com/components/
; TODO big opportunity here for cljx obviously
;------------------------------------------------------------

; use this to factor out extra templating somehow?
; => (grid [1 2 3] ["a" "b" "c"])
; ([:div.col-xs-1 "a"] [:div.col-xs-2 "b"] [:div.col-xs-3 "c"])

(defn grid [nums vals]
  (let [to-tag-kw #(-> (str "div.col-xs-" %) keyword)
        converter #(if (keyword? %) % (to-tag-kw %))
        mapper    #(vector (converter %1) %2)]
    [:div.row
     (map mapper nums vals)]))

(defn buttongroup
  [coll]
  [:div.btn-group
   (let-map
     [x coll]
     [:button {:type "button" :class "btn btn-default"} x])])

(defn dropdown
  "Create a bootstrap dropdown div dropdown.  Each item is used as is."
  [coll]
  [:div.dropdown
   [:ul.dropdown-menu {:role "menu"}
    (let-map 
      [x coll]
      [:li {:role "presentation"}
       [:a {:role "menuitem" :tabindex -1 :href "#"} x]])]])

; http://clojurefun.wordpress.com/2012/08/13/keyword-arguments-in-clojure/
(defn table
  "Create a bootstrap table
   headers is a collection of labels
   values is the collection of collections of rows
   :panel-heading and :panel-body are title and title sections"
  [headers trs & {:keys [panel-heading panel-body id class]
                  :or   {id            nil
                         class         nil
                         panel-heading nil
                         panel-body    nil}}]
  [:div.panel.panel-default
     (if panel-heading [:div.panel-heading panel-heading])
     (if panel-body [:div.panel-body panel-body])
     [:table {:id id :class class} 
      [:thead [:tr (let-map [h headers] [:th h])]]
      [:tbody trs]]])

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