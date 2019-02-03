(ns datum.album.show-overview
  (:require [datum.album :refer [Overview]]))

(defprotocol Api
  (overview [this album-id k]))

(defprotocol Transaction
  (get-state [this])
  (update-state [this f]))


(defrecord State [overview])

(defn execute [transaction api album-id]
  (let [state (get-state transaction)]
    (when (or (not (-> state :overview))
              (not (= (-> state :overview :album-id) album-id)))
      (overview api album-id (fn [overview]
       (update-state transaction #(assoc % :overview overview)))))))
