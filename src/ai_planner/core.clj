(ns ai-planner.core
  (:require [pddl-parse-and-ground.core :refer [parse_and_ground]])
  (:require [ai-planner.bfs :refer [best-first-search]])
  (:gen-class))

(defn complete-initial-state [initial-state ground-predicates]
  (cond (empty? ground-predicates) initial-state
        (not (some #(= (first ground-predicates) %) initial-state)) (complete-initial-state (concat (list {:operator 'not :atom (first ground-predicates)}) initial-state) (rest ground-predicates))
        :else (complete-initial-state initial-state (rest ground-predicates))))

(defn -main [& args]
  (let [parsed (parse_and_ground (first args) (second args))]
    (println (best-first-search
              (complete-initial-state (get-in parsed [:PDDLProblem :init]) (get-in parsed [:PDDLDomain :grounding :predicates]))
              (get-in parsed [:PDDLDomain :grounding :actions])
              (get-in parsed [:PDDLProblem :goal])))))
