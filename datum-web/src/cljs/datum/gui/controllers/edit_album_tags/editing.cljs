(ns datum.gui.controllers.edit-album-tags.editing
  (:require [datum.tag :as tag]
            [datum.tag.api]))

(defrecord State [tags attached-tag-set new-name])

(defrecord Context [state update-context on-save on-cancel])

(defn update-state [context f]
  (let [update-context (:update-context context)]
    (update-context #(update % :state f))))

(defn refresh-tags [context]
  (datum.tag.api/tags
   (fn [tags] (update-state context #(assoc % :tags tags)))))

(defn attach-tag [context tag]
  (update-state context #(update % :attached-tag-set tag/attach tag)))

(defn detach-tag [context tag]
  (update-state context #(update % :attached-tag-set tag/detach tag)))

(defn delete-tag [context tag]
  (detach-tag context tag)
  (datum.tag.api/delete-tag tag #(refresh-tags context)))


(defn change-name [context name]
  (update-state context #(assoc % :new-name name)))

(defn add-tag [context]
  (let [name (-> context :state :new-name)]
    (datum.tag.api/put-tags name
     (fn [_]
       (update-state context #(assoc % :new-name ""))
       (refresh-tags context)))))


(defn save [context]
  (let [{:keys [on-save state]} context]
    (on-save (tag/attached-tags (-> state :attached-tag-set)))))

(defn cancel [context]
  ((:on-cancel context)))
