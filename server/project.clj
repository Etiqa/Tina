;; project.clj

(defproject meta-config-server "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [org.clojure/data.json "0.2.6"]]

  :profiles {:dev
             {:dependencies [[lein-cljsbuild "1.1.7"]
                             [figwheel-sidecar "0.5.8"]]}}
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.16"]]
             :cljsbuild {:builds [{:id "prod"
                        :figwheel true
                        :source-paths ["src"]
                        :compiler {:main server.core
                                   :output-to "package/index.js"
                                   :target :nodejs
                                   :output-dir "target"
                                   ;; :externs ["externs.js"]
                                   :optimizations :none
                                   :pretty-print true
                                   :parallel-build true}}]})
