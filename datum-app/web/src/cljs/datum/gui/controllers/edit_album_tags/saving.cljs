(ns datum.gui.controllers.edit-album-tags.saving
  (:require [cljs.core.async :refer [go <! timeout]]
            [datum.album.api :as api]))

(defrecord State [status])

(defrecord Context [state update-context album-id attached-tags on-saved])

(defn update-state [context f]
  (let [update-context (:update-context context)]
    (update-context #(update % :state f))))

(defn run [context]
  (let [{:keys [album-id attached-tags]} context]
    (api/put-tags album-id attached-tags
     (fn []
       (go
         (<! (timeout 100))
         (update-state context #(assoc % :status ::saved)))))))

(defn on-saved [context]
  ((:on-saved context)))
