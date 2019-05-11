(ns datum.gui.controllers.edit-album-tags.loading
  (:require [datum.tag.api]
            [datum.album.api]))

(defrecord State [tags attached-tags])

(defrecord Context [state update-context album-id on-loaded])

(defn update-state [context f]
  (let [update-context (:update-context context)]
    (update-context #(update % :state f))))

(defn run [context]
  (datum.tag.api/tags
   (fn [tags]
     (update-state context #(assoc % :tags tags))))
  (datum.album.api/tags (:album-id context)
   (fn [tags]
     (update-state context #(assoc % :attached-tags tags)))))

(defn on-loaded [context]
  ((:on-loaded context) (:state context)))
