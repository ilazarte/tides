(ns tides.clientviews)

;------------------------------------------------------------
; Bootstrap components
; http://getbootstrap.com/components/
; copied source from tide components since project not enabled with cljx
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
   (for
     [x coll]
     [:button {:type "button" :class "btn btn-default"} x])])

(defn dropdown
  "Create a bootstrap dropdown div dropdown.  Each item is used as is."
  [coll]
  [:div.dropdown
   [:ul.dropdown-menu {:role "menu"}
    (for 
      [x coll]
      [:li {:role "presentation"}
       [:a {:role "menuitem" :tabindex -1 :href "#"} x]])]])

(defn table
  "Create a bootstrap table
   headers is a collection of labels
   values is the collection of collections of rows
   :panel-heading and :panel-body are title and title sections"
  [headers trs & {:keys [id class] :or {id    nil
                                        class nil}}]
  [:table {:id id :class class} 
   [:thead [:tr (for [h headers] [:th h])]]
   [:tbody trs]])