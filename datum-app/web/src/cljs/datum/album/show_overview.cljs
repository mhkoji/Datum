(ns datum.album.show-overview
  (:require [cljs.core.async :refer [go <! timeout]]))

(defprotocol Api
  (name [this album-id k])
  (pictures [this album-id k]))

(defprotocol Transaction
  (get-state [this])
  (update-state [this f]))


(defrecord State [album-id name pictures])

(defn execute [transaction api]
  (let [state (get-state transaction)]
    ;; Name
    (when (not (state :name))
      (name api (state :album-id) (fn [name]
       (update-state transaction #(assoc % :name name)))))
    ;; Pictures
    (when (not (state :pictures))
      (pictures api (state :album-id) (fn [pictures]
       (update-state transaction #(assoc % :pictures pictures)))))))
