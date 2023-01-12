(defproject ai_planner "0.1.0"
  :description "An AI Planner supporting STRIPS with typing and negative preconditions"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"] [org.clojars.existenzquantor/pddl-parse-and-ground "0.1.2"]]
  :main ^:skip-aot ai-planner.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
