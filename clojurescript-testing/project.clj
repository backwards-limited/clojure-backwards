(defproject clojurescript-testing "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [cljs-http "0.1.46"]
                 [org.clojure/test.check "0.10.0"]
                 [funcool/cuerdas "2.2.0"]]
  :plugins [[lein-doo "0.1.11"]]
  :repl-options {:init-ns clojurescript-testing.core}
  :cljsbuild {:builds
              {:test {:source-paths ["src" "test"]
                      :compiler {:output-to "out/tests.js"
                                 :output-dir "out"
                                 :main clojurescript-testing.runner
                                 :optimizations :none}}}})
