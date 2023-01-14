(ns ai-planner.bfs
  (:require [clojure.set :refer [subset?]])
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
    (extract-plan-recur (node :parent) (concat (list (get-in node [:action :action :name])) plan))))

(defn extract-plan [node]
  (extract-plan-recur node '()))

(defn best-first-search-recur [actions goal queue]
  (if (seq queue) 
    (let [node (first queue)
          applicable-actions (filter (partial applicable? (get-in node [:state])) actions)]
      (if (subset? (set goal) (set (get-in node [:state])))
        (extract-plan node) 
        (if (seq applicable-actions)
          (best-first-search-recur actions goal (concat (rest queue) (map (partial apply-action node) applicable-actions)))
          nil)))
    nil))

(defn best-first-search [initial-state actions goal]
  (best-first-search-recur actions (formula-as-list goal) (list {:state initial-state :action nil :parent nil})))