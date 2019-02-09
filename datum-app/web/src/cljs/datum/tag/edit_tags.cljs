(ns datum.tag.edit-tags
  (:require [cljs.core.async :refer [<! go timeout]]
            [datum.tag :as tag-api]))

(defprotocol Api
  (delete-tag [this tag k])
  (submit-new-tag [this name k]))

(defprotocol Transaction
  (get-state [this])
  (update-state [this f]))


(defrecord State [tags name])

(defn submit [transaction api k]
  (letfn [(handle-submitted []
            (update-state transaction #(assoc % :name nil))
            (k))]
    (when-let [name (-> transaction get-state :name)]
      (submit-new-tag api name handle-submitted))))

(defn set-name [transaction name]
  (update-state transaction #(assoc % :name name)))


(defn delete [transaction api tag k]
  (letfn [(handle-deleted []
            (load-tags transaction api)
            (k))]
    (delete-tag api tag handle-deleted)))
