(ns ai-planner.core
  (:require [clojure.data.json :as json])
  (:require [pddl-parse-and-ground.core :refer [parse-and-ground]])
  (:require [ai-planner.search.bfs :as bfs])
  (:require [ai-planner.search.dfs :as dfs])
  (:require [ai-planner.search.iddfs :as iddfs])
  (:require [ai-planner.search.tabu :as tabu])
  (:gen-class))

(defn complete-initial-state [initial-state ground-predicates]
  (cond (empty? ground-predicates) initial-state
        (not (some #(= (first ground-predicates) %) initial-state)) (complete-initial-state (concat (list {:operator 'not :atom (first ground-predicates)}) initial-state) (rest ground-predicates))
        :else (complete-initial-state initial-state (rest ground-predicates))))

(defn -main [& args]
  (let [parsed (parse-and-ground (first args) (second args))
        init (distinct (complete-initial-state (get-in parsed [:PDDLProblem :init]) (get-in parsed [:PDDLDomain :grounding :predicates])))
        grounded-actions (get-in parsed [:PDDLDomain :grounding :actions])
        goal (get-in parsed [:PDDLProblem :goal])]
    (cond
      (and (= (count args) 3) (= "dfs" (nth args 2))) (json/pprint (dfs/run init grounded-actions goal)) 
      (and (= (count args) 3) (= "iddfs" (nth args 2))) (json/pprint (iddfs/run init grounded-actions goal))
      (and (= (count args) 3) (= "tabu" (nth args 2))) (json/pprint (tabu/run init grounded-actions goal))
      :else     (json/pprint (bfs/run init grounded-actions goal)))))
