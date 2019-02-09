(ns datum.tag.edit-content-tags
  (:require [cljs.core.async :refer [<! go timeout]]
            [datum.tag :as tag-api]))

(defprotocol Api
  (get-attached-tags [this k])
  (set-attached-tags [this k]))


(defprotocol Transaction
  (get-state [this])
  (update-state [this f]))


(defrecord State [tags attached-tag-set])

(defn start [transaction api]
  (get-attached-tags api (fn [tags]
    (let [set (datum.tag/AttachedTagSet. tags)]
      (update-state transaction #(assoc % :attached-tag-set set))))))

(defn submit [transaction api k]
  (let [state         (get-state transaction)
        attached-tags (-> state
                          :attached-tag-set
                          datum.tag/attached-tags)]
    (set-attached-tags api attached-tags
     #(go (<! (timeout 1000)) (k)))))

(defn attach-tag [transaction tag]
  (update-state transaction #(update % :state datum.tag/attach tag)))

(defn detach-tag [transaction tag]
  (update-state transaction #(update % :state datum.tag/detach tag)))
