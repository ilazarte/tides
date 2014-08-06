(defproject tides "0.0.1-SNAPSHOT"
  :description "A learning project using stocks"
  :url "https://github.com/ilazarte/tides"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  :min-lein-version "2.3.4"
  
  :source-paths ["src/clj" "src/cljs"]

  ; watch out for https://github.com/holmsand/reagent/issues/39
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2280"]
                 [clj-time "0.5.1"]
                 [compojure "1.1.6"]
                 [ring/ring-json "0.2.0"]
                 [org.webjars/bootstrap "3.2.0"]
                 [org.webjars/jquery "2.1.1"]
                 [org.webjars/react "0.9.0"]
                 [org.webjars/d3js "3.4.9"]
                 [reagent "0.4.2"]
                 [jayq "2.5.1"]
                 [impetus "0.1.0-SNAPSHOT"]
                 [arbol "0.1.0-SNAPSHOT"]
                 [cljd3 "0.1.0-SNAPSHOT"]]
  
  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [com.cemerick/austin "0.1.3"]
            [lein-ring "0.8.10"]]
  
  :ring {:handler tides.handler/app
         :auto-reload? true
         :auto-refresh? true}
  
  :cljsbuild
  {:builds {:tides
            {:source-paths ["src/cljs"]
             :compiler
             {:output-to "dev-resources/public/js/tides.js"
              :optimizations :none
              :pretty-print false}}}})
