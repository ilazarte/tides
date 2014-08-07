(ns tides.handler
  (:require
    [compojure.core           :refer [defroutes GET]] 
    [compojure.handler        :as handler]
    [compojure.route          :as route]
    [ring.adapter.jetty       :as jetty]
    [ring.middleware.json     :as json]
    [ring.middleware.resource :as resource]
    [ring.middleware.content-type :as content-type]
    [ring.util.response       :as response] 
    [tides.views              :as views]
    [arbol.core               :as tree]
    [impetus.core             :as impetus]
    [cljd3.css                :as css])
  (:gen-class))

; json: https://github.com/ring-clojure/ring-json
; from parkway was: (take-last 40 (core/make-price-ma-ratio-list core/index-watchlist))
; start the server using "start-web"
; use body tag to return json into the source.

; while run/start-web is for the cljs-start template
; others advocate using lein command (lein ring server-headless 8080)
; http://stackoverflow.com/questions/1665760/compojure-development-without-web-server-restarts
; whatever that command does, it works better than run.

(def main-watchlist 
  ["AAPL", "AMZN", "BAC", "BIDU", "CAT",
   "DIA", "EEM", "EWZ", "FCX", "FFIV", 
   "FSLR", "GLD", "GOOG", "GS", "IBM",
   "IWM", "JPM", "KO", "MCD", "NFLX", 
   "PG", "PM", "QCOM", "QQQ", "SLV", 
   "SPY", "USO", "WMT", "XOM"])

(def test-watchlist
  ["AAPL" "GOOG"])

(defn last-values [n prices]
  "remove all by the last n observations"
  (tree/climb prices [:values :vec] #(take-last n %) vec))

(defroutes app-routes
  
  (GET "/" [] (views/index))
 
  (GET "/learn" [] (views/single-div :div#tides))
  
  (GET "/css/line-chart.css" [] {:headers {"Content-Type" "text/css"}
                                 :body (css/line)})
  
  (GET "/relative" [params]
       {:body "hello worlds"})
  
  (GET "/impetus" [params]
       {:body (last-values 40 (impetus/make-price-ma-ratio-list main-watchlist))})
  
  (GET "/impetus-test" [params]
       {:body (last-values 40 (impetus/make-price-ma-ratio-list test-watchlist))})
  
  (route/resources "/")
  
  (route/not-found (views/file-not-found)))

(def app
  (-> (handler/site app-routes)
    (resource/wrap-resource "/META-INF/resources")
    (resource/wrap-resource "/public")
    (content-type/wrap-content-type)
    json/wrap-json-body
    json/wrap-json-params
    json/wrap-json-response))


(defn -main [& args]
  (jetty/run-jetty app {:port 80}))