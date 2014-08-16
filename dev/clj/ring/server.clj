(ns ring.server
  (:require [ring.adapter.jetty        :as jetty] 
            [compojure.handler         :as compojure]
            [tides.handler             :as handler]
            [ring.middleware.json      :as json]
            [ring.middleware.file      :as file]
            [ring.middleware.file-info :as file-info]
            [ring.middleware.resource  :as resource]
            [ring.middleware.content-type :as content-type]))

(def app
  (-> (compojure/site handler/app-routes)
    (resource/wrap-resource "/META-INF/resources")
    (file/wrap-file "target/dev/public")
    (content-type/wrap-content-type)
    json/wrap-json-body
    json/wrap-json-params
    json/wrap-json-response))

(defn start []
  (defonce server
    (jetty/run-jetty app {:port 8080 :join? false}))
  server)

(defn stop []
  (.stop server))