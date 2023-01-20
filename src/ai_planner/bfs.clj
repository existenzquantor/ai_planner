(ns ai-planner.bfs
  (:require [clojure.set :refer [subset?]])
  (:require [ai-planner.general :refer[applicable? extract-plan formula-as-list apply-action has-distinct-state?]]))

(defn best-first-search-recur [actions goal queue] 
  (if (seq queue) 
    (let [node (first queue)
          applicable-actions (filter (partial applicable? (get-in node [:state])) actions)]
      (if (subset? (set goal) (set (get-in node [:state])))
        {:plan (extract-plan node)}
        (if (seq applicable-actions)
          (best-first-search-recur actions goal (concat (rest queue) (filter has-distinct-state? (map (partial apply-action node) applicable-actions))))
          nil)))
    nil))

(defn best-first-search [initial-state actions goal]
  (best-first-search-recur actions (formula-as-list goal) (list {:state initial-state :action nil :parent nil})))