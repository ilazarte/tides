(defproject tides "0.0.1-SNAPSHOT"
  :description "A learning project using stocks"
  :url "https://github.com/ilazarte/tides"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  :min-lein-version "2.3.4"
  
  :source-paths ["src/clj" "src/cljs"]
  
  :clean-targets [:target-path]
  
  ; watch out for https://github.com/holmsand/reagent/issues/39
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2280"]
                 [clj-time "0.5.1"]
                 [ring "1.2.1"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring/ring-json "0.2.0"]
                 [org.webjars/bootstrap "3.2.0"]
                 [org.webjars/jquery "2.1.1"]
                 [org.webjars/react "0.9.0"]
                 [org.webjars/d3js "3.4.9"]
                 [reagent "0.4.2"]
                 [jayq "2.5.1"]
                 [impetus "0.1.0-SNAPSHOT"]
                 [arbol "0.1.0-SNAPSHOT" :exclusions [org.clojure/clojure]]
                 [cljd3 "0.1.0-SNAPSHOT" :exclusions [org.clojure/clojure]]]
  
  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [lein-ring "0.8.10"]]
    
  :ring {:handler tides.handler/app}
  
  :aliases {"once"      ["cljsbuild" "once"]
            "auto"      ["cljsbuild" "auto"] 
            "headless"  ["ring" "server-headless" "8080"]
            "server"    ["ring" "server" "8080"]
            "dev"       ["pdo" "headless," "cljsbuild" "auto"]
            "uberbuild" ["with-profile" "prod" "do" "clean," "cljsbuild" "once," "uberjar"]}
  
  :profiles {:dev {:source-paths   ["dev/clj" "dev/cljs"]
                   :dependencies   [[ccw/ccw.server "0.1.0"]]
                   :repl-options   {:init (require 'ccw.debug.serverrepl)}
                   :plugins [[com.cemerick/austin "0.1.3"]
                             [lein-pdo "0.1.1"]]
                   :ring    {:handler       ring.server/app
                             :auto-reload?  true
                             :auto-refresh? true}
                   :cljsbuild {:builds {:tides {:source-paths ["src/cljs" "dev/cljs"]
                                                :incremental true
                                                :compiler
                                                {:optimizations :none
                                                 :output-dir "target/dev/public/js"
                                                 :output-to  "target/dev/public/js/tides.js"
                                                 :source-map "target/dev/public/js/tides.js.map"
                                                 :pretty-print true}}}}
                   :injections [(require '[ring.server :as http :refer [start stop]]
                                         'cemerick.austin.repls)
                                (defn browser-repl []
                                  (cemerick.austin.repls/cljs-repl (reset! cemerick.austin.repls/browser-repl-env
                                                                           (cemerick.austin/repl-env))))]}

             :prod {:main tides.handler
                    :aot  [tides.handler]
                    :resource-paths ["target/prod"]
                    :cljsbuild {:builds {:tides
                                         {:source-paths ["src/cljs"]
                                          :compiler
                                          {:output-to  "target/prod/public/js/tides.js"
                                           :source-map "target/prod/public/js/tides.js.map"
                                           :output-dir "target/prod/public/js" 
                                           :optimizations :none}}}}}})