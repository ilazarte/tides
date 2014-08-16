(ns tides.app
  (:require
    [ring.adapter.jetty :as jetty]
    [tides.handler      :as handler])
  (:gen-class))

(defn -main [& args]
  (jetty/run-jetty handler/app {:port 80}))
