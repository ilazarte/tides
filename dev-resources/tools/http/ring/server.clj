(ns ring.server
  (:require [cemerick.austin.repls :refer (browser-connected-repl-js)] 
            [compojure.route :refer  (resources)]
            [compojure.core :refer (GET defroutes)]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :as reload] 
            [clojure.java.io :as io]
            [tides.handler :as handler]))

(defn start []
  (defonce server
    (jetty/run-jetty (reload/wrap-reload handler/app) {:port 8080 :join? false}))
  server)

(defn stop []
  (.stop server))

(defn restart []
  (.start server))