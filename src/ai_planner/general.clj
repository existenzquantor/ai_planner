(ns ai-planner.general
  (:require [pddl-parse-and-ground.parser :refer [in?]])
  (:require [clojure.set :refer [subset? intersection]]))

(defn goal? [goal-set state-set] 
  (subset? goal-set state-set))

(defn number-of-common-literals [set1 set2]
  (count (intersection set1 set2)))

(defn formula-as-list [formula]
  (if (= 'and (get-in formula [:operator]))
    (get-in formula [:conjuncts])
    (list formula)))

(defn negation-of-atom [atom]
  (if (= 'not (get-in atom [:operator]))
    (get-in atom [:atom])
    {:operator 'not :atom atom}))

(defn delete-effect [effect state]
  (filter #(not (in? effect %)) state))

(defn apply-action [node action]
  (let [state (get-in node [:state])
        effect (formula-as-list (get-in action [:effect]))]
    {:state (distinct (concat (delete-effect (map negation-of-atom effect) state) effect)) :action action :parent node}))

(defn applicable? [state action]
  (cond
    (empty? (get-in action [:precondition])) true
    :else (subset? (set (formula-as-list (get-in action [:precondition]))) (set state))))

(defn- extract-plan-recur [node plan]
  (if (nil? (node :parent))
    plan
    (extract-plan-recur (node :parent) (concat (list {:name (get-in node [:action :name]) :parameters (map :symbol (get-in node [:action :parameters]))}) plan))))

(defn extract-plan [node]
  (extract-plan-recur node '()))

(defn find-state-in-parents [state current]
  (cond
    (nil? (current :action)) false
    (nil? ((current :parent) :action)) (= state ((current :parent) :state))
    (not= state ((current :parent) :state)) (find-state-in-parents state (current :parent))
    :else true))

(defn has-distinct-state? [node]
  (not (find-state-in-parents (node :state) node)))
