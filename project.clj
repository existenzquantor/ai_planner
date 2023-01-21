(defproject org.clojars.existenzquantor/ai_planner "0.1.8"
  :description "An AI Planner supporting STRIPS with typing and negative preconditions"
  :url "https://github.com/existenzquantor/ai_planner"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"] [org.clojars.existenzquantor/pddl-parse-and-ground "0.2.2"]]
  :main ^:skip-aot ai-planner.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :sign-releases false}]]
  :min-lein-version "2.0.0")
