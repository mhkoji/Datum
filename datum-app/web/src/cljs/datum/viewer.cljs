(ns datum.viewer)

(defprotocol Transaction
  (update-state [this f]))

(defrecord State [images index size])


(defn increment-index [transaction diff]
  (update-state transaction (fn [state]
    (let [images (-> state :images)
          added-index (+ (-> state :index) diff)
          max-index (dec (count images))
          new-index (cond (< added-index 0) 0
                          (< max-index added-index) max-index
                          :else added-index)]
      (assoc state :index new-index)))))


(defn set-size [transaction size]
  (update-state transaction #(assoc % :size size)))
