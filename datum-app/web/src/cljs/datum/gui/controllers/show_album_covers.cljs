(ns datum.gui.controllers.show-album-covers
  (:require [cljs.core.async :refer [go <! timeout]]))

(defprotocol Api
  (covers [this k]))

(defprotocol Transaction
  (update-state [this f]))

(defrecord Context [transaction api])

(defn call-partitioned [xs on-processing on-finished]
  (go (loop [[sub-xs & rest] (partition 100 100 [] xs)]
        (if (empty? sub-xs)
          (on-finished)
          (do (on-processing sub-xs)
              (<! (timeout 100))
              (recur rest))))))

(defn run [context]
  (let [{:keys [transaction api]} context]
    (update-state transaction
     (fn [_] {:type :loading :covers []}))
    (covers api
     (fn [covers]
       (update-state transaction #(assoc % :type :appending))
       (call-partitioned covers
        (fn [sub]
          (update-state transaction #(update % :covers concat sub)))
        (fn []
          (update-state transaction #(assoc % :type :finished))))))))
