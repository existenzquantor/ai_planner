(ns ai-planner.search.tabu
  (:require [ai-planner.general :refer [number-of-common-literals applicable? extract-plan formula-as-list apply-action has-distinct-state? goal?]]))

(defn all-best-nodes [maxnum nums nodes best]
  (if (empty? nums)
    best
    (if (= maxnum (first nums))
      (all-best-nodes maxnum (rest nums) (rest nodes) (conj best (first nodes)))
      (all-best-nodes maxnum (rest nums) (rest nodes) best))))

(defn get-best-node [goal nodes]
  (let [nums (map #(number-of-common-literals (set goal) (set (% :state))) nodes)
        maxnum (apply max nums)]
    (rand-nth (all-best-nodes maxnum nums nodes '()))))

(defn tabu-search-recur [initial-state actions goal node]
  (if (goal? (set goal) (set (node :state)))
    {:plan (extract-plan node)}
    (let [applicable-actions (filter (partial applicable? (node :state)) actions)
          new-nodes (map (partial apply-action node) applicable-actions)
          non-tabu-nodes (filter #(has-distinct-state? %) new-nodes)]
      (if (empty? non-tabu-nodes)
        (tabu-search-recur initial-state actions goal {:state initial-state :action nil :parent nil})
        (tabu-search-recur initial-state actions goal (get-best-node goal non-tabu-nodes))))))

(defn run [initial-state actions goal]
  (tabu-search-recur initial-state actions (formula-as-list goal) {:state initial-state :action nil :parent nil}))