(ns datum.gui.controllers.show-album-overview
  (:require [datum.album :refer [Overview]]))

(defprotocol Api
  (overview [this album-id k]))

(defprotocol Transaction
  (update-context [this f]))

(defrecord State [overview])

(defrecord Context [state transaction api album-id])

(defn update-state [context f]
  (update-context (-> context :transaction) #(update % :state f)))

(defn run [context]
  (let [{:keys [state album-id api]} context]
    (when (or (not state)
              (not (-> state :overview))
              (not (= (-> state :overview :album-id) album-id)))
      (overview api album-id
       (fn [overview] (update-state context #(State. overview)))))))
