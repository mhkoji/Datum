(ns datum.album.show-covers
  (:require [cljs.core.async :refer [go <! timeout]]))

(defprotocol Api
  (covers [this offset count k]))

(defprotocol Transaction
  (update-state [this f]))


(defrecord State [covers loading-p])


(defn call-partitioned [xs on-processing on-finished]
  (go (loop [[sub-xs & rest] (partition 100 100 [] xs)]
        (if (empty? sub-xs)
          (on-finished)
          (do (on-processing sub-xs)
              (<! (timeout 100))
              (recur rest))))))

(defn execute [transaction api offset count]
  (update-state transaction
   #(-> %
        (assoc :covers nil)
        (assoc :loading-p true)))
  (covers api offset count (fn [covers]
   (call-partitioned covers
    (fn [sub]
      (update-state transaction #(update % :covers concat sub)))
    (fn []
      (update-state transaction #(assoc % :loading-p false)))))))
