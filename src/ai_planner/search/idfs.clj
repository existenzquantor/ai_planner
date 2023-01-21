(ns ai-planner.search.idfs
    (:require [ai-planner.general :refer [applicable? extract-plan formula-as-list apply-action has-distinct-state? goal?]]))

(defn num-parents-recur [node c]
  (if (nil? (node :parent))
    c
    (num-parents-recur (node :parent) (+ 1 c))))

(defn num-parents [node]
  (num-parents-recur node 0))

(defn iterative-depth-first-search-recur [i initial-state actions goal queue]
  (let [queue-new (filter #(> (+ 1 i) (num-parents %)) queue)]
    (if (empty? queue-new)
      (iterative-depth-first-search-recur (+ i 1) initial-state actions goal (list {:state initial-state :action nil :parent nil}))
      (when (seq queue-new)
        (let [node (first queue-new)
              applicable-actions (filter (partial applicable? (get-in node [:state])) actions)]
          (if (goal? (set goal) (set (get-in node [:state])))
            {:plan (extract-plan node)}
            (when (seq applicable-actions)
              (iterative-depth-first-search-recur i initial-state actions goal (concat (filter has-distinct-state? (map (partial apply-action node) applicable-actions)) (rest queue-new))))))))))

(defn run [initial-state actions goal]
  (iterative-depth-first-search-recur 1 initial-state actions (formula-as-list goal) (list {:state initial-state :action nil :parent nil})))