(ns ai-planner.search.bfs
  (:require [ai-planner.general :refer [applicable? extract-plan formula-as-list apply-action has-distinct-state? goal?]]))

(defn best-first-search-recur [actions goal queue]
  (when (seq queue)
    (let [node (first queue)
          applicable-actions (filter (partial applicable? (get-in node [:state])) actions)]
      (if (goal? (set goal) (set (get-in node [:state])))
        {:plan (extract-plan node)}
        (when (seq applicable-actions)
          (best-first-search-recur actions goal (concat (rest queue) (filter has-distinct-state? (map (partial apply-action node) applicable-actions)))))))))

(defn run [initial-state actions goal]
  (best-first-search-recur actions (formula-as-list goal) (list {:state initial-state :action nil :parent nil})))

