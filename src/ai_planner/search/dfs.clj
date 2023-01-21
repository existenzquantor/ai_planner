(ns ai-planner.search.dfs
  (:require [clojure.set :refer [subset?]])
  (:require [ai-planner.general :refer [applicable? extract-plan formula-as-list apply-action has-distinct-state?]]))

(defn depth-first-search-recur [actions goal queue]
  (when (seq queue)
    (let [node (first queue)
          applicable-actions (filter (partial applicable? (get-in node [:state])) actions)]
      (if (subset? (set goal) (set (get-in node [:state])))
        {:plan (extract-plan node)}
        (when (seq applicable-actions)
          (depth-first-search-recur actions goal (concat (filter has-distinct-state? (map (partial apply-action node) applicable-actions)) (rest queue))))))))

(defn run [initial-state actions goal]
  (depth-first-search-recur actions (formula-as-list goal) (list {:state initial-state :action nil :parent nil})))