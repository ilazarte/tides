(defproject tides "0.0.1-SNAPSHOT"
  
  :description "A learning project using stocks"
    
  :url "https://github.com/ilazarte/tides"
  
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  :min-lein-version "2.4.3"
  
  :source-paths ["src/clj" "src/cljs"]
  
  :clean-targets [:target-path "classes" "bin"]
  
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2511"]
                 [clj-time "0.9.0"]
                 [ring "1.3.2"]
                 [compojure "1.3.1"]
                 [hiccup "1.0.5"]
                 [ring/ring-json "0.3.1"]
                 [org.webjars/bootstrap "3.3.1"]
                 [org.webjars/jquery "2.1.3"]
                 [org.webjars/react "0.11.1"]
                 [org.webjars/d3js "3.5.2"]
                 [whoops/reagent "0.4.3"]
                 [jayq "2.5.2"]
                 [impetus "0.1.0-SNAPSHOT"]
                 [ilazarte/arbol "0.1.3" :exclusions [org.clojure/clojure]]
                 [cljd3 "0.1.0-SNAPSHOT" :exclusions [org.clojure/clojure]]]
  
  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-ring "0.8.13"]]
   
  ; TODO will enable this once prod profile uses advanced compilation
  ;:prep-tasks [["once"]]
  
  :ring {:handler tides.handler/app}
  
  :aliases {"once"      ["cljsbuild" "once"]
            "auto"      ["cljsbuild" "auto"] 
            "headless"  ["ring" "server-headless" "8080"]
            "server"    ["ring" "server" "8080"]
            "dev"       ["pdo" "headless," "cljsbuild" "auto"]
            "uberbuild" ["with-profile" "production" "do" "clean," "cljsbuild" "once," "uberjar"]}
  
  :profiles {:dev {:source-paths   ["dev/clj" "dev/cljs"]
                   :dependencies   [[ccw/ccw.server "0.1.1"]
                                    [javax.servlet/servlet-api "2.5"]]
                   :repl-options   {:init (require 'ccw.debug.serverrepl)}
                   :plugins [[lein-pdo "0.1.1"]]
                   :main    nil
                   :aot     []
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
                                                 :pretty-print true}}}}}

             :production {:main tides.app
                          :aot  [tides.app]
                          :resource-paths ["target/prod"]
                          :cljsbuild {:builds {:tides
                                               {:source-paths ["src/cljs"]
                                                :compiler
                                                {:output-to  "target/prod/public/js/tides.js"
                                                 :source-map "target/prod/public/js/tides.js.map"
                                                 :output-dir "target/prod/public/js" 
                                                 :optimizations :none}}}}}})