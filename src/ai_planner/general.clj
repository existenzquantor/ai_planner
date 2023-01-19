(ns ai-planner.general
  (:require [pddl-parse-and-ground.parser :refer [in?]]))

(defn formula-as-list [formula]
  (if (= 'and (get-in formula [:operator]))
    (get-in formula [:conjuncts])
    (list formula)))

(defn negation-of-atom [atom]
  (if (= 'not (get-in atom [:operator]))
    (get-in atom [:atom])
    {:operator 'not :atom atom}))

(defn delete-effect [effect state]
  (if (empty? effect)
    state
    (delete-effect (rest effect) (remove #(= (first effect) %) state))))

(defn apply-action [node action]
  (let [state (set (get-in node [:state]))
        effect (formula-as-list (get-in action [:action :effect]))
        del (delete-effect effect state)
        state-new (concat (delete-effect (map negation-of-atom effect) del) effect)]
    {:state state-new :action action :parent node}))

(defn all-preconditions-met? [preconditions state]
  (cond
    (empty? preconditions) true
    (not (in? state (first preconditions))) false
    :else (all-preconditions-met? (rest preconditions) state)))

(defn applicable? [state action]
  (cond
    (empty? (get-in action [:action :precondition])) true
    :else (all-preconditions-met? (formula-as-list (get-in action [:action :precondition])) state)))

(defn- extract-plan-recur [node plan]
  (if (nil? (node :parent))
    plan
    (extract-plan-recur (node :parent) (concat (list {:name (get-in node [:action :action :name]) :parameters (map :symbol (get-in node [:action :action :parameters]))}) plan))))

(defn extract-plan [node]
  (extract-plan-recur node '()))