(ns tides.util)

(defn log [msg]
  (js/console.log msg))

(defn add-key-default [stocks]
  "add a key default to each of the stock datasets" 
  (map #(assoc-in % [:key] (:symbol %)) stocks))

(defn parse-date-in-values [stocks]
  "convert the date strings to js date instances" 
  (let [parse   #(-> (js/Date.parse %) js/Date.)
        updater #(update-in % [:x] parse)
        to-date #(map updater %)]
    (map #(update-in % [:values] to-date) stocks)))

(defn post-process-data [raw]
  "convert raw json values into a clojure data set" 
  (let [cljdata   (js->clj raw :keywordize-keys true)
        typeddata (parse-date-in-values cljdata)
        keyeddata (add-key-default typeddata)]
    keyeddata))

(defn load-data
  ([url success] (load-data url success #(log %)))
  ([url success failure]
    "invoke the callbacks either data or error message" 
    (js/d3.json 
      url
      (fn [err json]
        (if err
          (failure err)
          (success json))))))