(ns datum.gui.controllers.show-album-covers
  (:require [cljs.core.async :refer [go <! timeout]]))

(defprotocol Api
  (covers [this k]))

(defprotocol Transaction
  (update-context [this f]))

(defrecord State [type covers])

(defrecord Context [state transaction api])

(defn update-state [context f]
  (update-context (-> context :transaction) #(update % :state f)))


(defn call-partitioned [xs on-processing on-finished]
  (go (loop [[sub-xs & rest] (partition 100 100 [] xs)]
        (if (empty? sub-xs)
          (on-finished)
          (do (on-processing sub-xs)
              (<! (timeout 100))
              (recur rest))))))

(defn run [context]
  (update-state context #(State. :loading []))
  (covers (-> context :api)
   (fn [covers]
     (update-state context #(assoc % :type :appending))
     (call-partitioned covers
      (fn [sub]
        (update-state context #(update % :covers concat sub)))
      (fn []
        (update-state context #(assoc % :type :finished)))))))
