(ns ring.server
  (:require [ring.adapter.jetty        :as jetty] 
            [compojure.handler         :as compojure]
            [tides.handler             :as handler]
            [ring.middleware.json      :as json]
            [ring.middleware.file      :as file]
            [ring.middleware.file-info :as file-info]
            [ring.middleware.resource  :as resource]
            [ring.middleware.content-type :as content-type]))

(defn wrap-nocache 
  "completely disable all caching on the client, helps with source maps accuracy and update to dateness"
  [handler]
  (fn [request]
    (let [response (handler request)]
    (-> response
      (assoc-in [:headers "Cache-Control"] "no-cache, no-store, must-revalidate")
      (assoc-in [:headers "Pragma"] "no-cache")
      (assoc-in [:headers "Expires"] "0")))))

(def app
  (-> (compojure/site handler/app-routes)
    (resource/wrap-resource "/META-INF/resources")
    (file/wrap-file "target/dev/public")
    (content-type/wrap-content-type)
    json/wrap-json-body
    json/wrap-json-params
    json/wrap-json-response
    wrap-nocache))

(defn start []
  (defonce server
    (jetty/run-jetty app {:port 8080 :join? false}))
  server)

(defn stop []
  (.stop server))