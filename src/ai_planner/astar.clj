(ns ai-planner.astar
  (:require [clojure.set :refer [subset?]])
  (:require [ai-planner.general :refer [applicable? extract-plan formula-as-list apply-action]])
  (:require [clojure.set :refer [difference]]))

(defn dist-to-goal [goal node]
  (assoc-in node [:h] (count (difference (set goal) (set (node :state))))))

(defn a-star-search-recur [actions goal queue]
  (if (seq queue)
    (let [node (first (sort #(< (+ (%1 :g) (%1 :h)) (+ (%2 :g) (%2 :h))) queue))
          applicable-actions (filter (partial applicable? (get-in node [:state])) actions)] 
      (if (subset? (set goal) (set (get-in node [:state])))
        {:plan (extract-plan node)}
        (if (seq applicable-actions)
          (let [new-nodes (map (partial apply-action node) applicable-actions)
                new-nodes-with-g (map #(assoc-in %1 [:g] (+ 1 (get-in %1 [:parent :g]))) new-nodes)
                new-nodes-with-g-and-h (map (partial dist-to-goal goal) new-nodes-with-g)]
            (a-star-search-recur actions goal (concat (rest queue) new-nodes-with-g-and-h)))
          nil)))
    nil))

(defn a-star-search [initial-state actions goal]
  (a-star-search-recur actions (formula-as-list goal) (list {:state initial-state :action nil :parent nil :g 0 :h 0})))