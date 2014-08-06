{:shared {:clean-targets ["out" :target-path]
          :resources-paths ["dev-resources"]
          :cljsbuild
          {:builds {:tides
                    {:compiler
                     {:output-dir "dev-resources/public/js"
                      :source-map "dev-resources/public/js/tides.js.map"}}}}}
 :dev    [:shared
          {:source-paths ["dev-resources/tools/http" "dev-resources/tools/repl"]
           :dependencies [[ring "1.2.1"]
                          [compojure "1.1.6"]
                          [ccw/ccw.server "0.1.0"]]
           :repl-options {:init (require 'ccw.debug.serverrepl)}
           :plugins [[com.cemerick/austin "0.1.3"]
                     [lein-pdo "0.1.1"]]
           :cljsbuild
           {:builds {:tides
                     {:source-paths ["dev-resources/tools/repl"]
                      :incremental true
                      :compiler
                      {:optimizations :none
                       :pretty-print true}}}}
           :aliases {"once"     ["cljsbuild" "once"]
                     "auto"     ["cljsbuild" "auto"] 
                     "headless" ["ring" "server-headless" "8080"]
                     "server"   ["ring" "server" "8080"]
                     "dev"      ["pdo" "headless," "cljsbuild" "auto"]}
           :injections [(require '[ring.server :as http :refer [start stop restart]]
                                 'cemerick.austin.repls)
                        (defn browser-repl []
                          (cemerick.austin.repls/cljs-repl (reset! cemerick.austin.repls/browser-repl-env
                                                                   (cemerick.austin/repl-env))))]}]}
