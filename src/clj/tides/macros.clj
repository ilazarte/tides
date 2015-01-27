(ns tides.macros)

; sort of an enhanced for loop for purposes and readability and indexing
; (foreach [i (range 5)] (inc i))
(defmacro foreach
  ";=> "
  [decl form] 
  (let [bindings (take-nth 2 decl)
        colls    (take-nth 2 (rest decl))]
    `(map (fn [~@bindings] ~form) ~@colls)))