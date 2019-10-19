(ns datum.gui.controllers.edit-album-tags.loading
  (:require [datum.tag.api]
            [datum.album.api]))

(defrecord State [tags attached-tags])

(defrecord Context [state get-context update-context album-id on-loaded])

(defn get-context [context f]
  ((:get-context context) f))

(defn update-context [context f]
  ((:update-context context) f))

(defn update-state [context f]
  (update-context context #(update % :state f)))

(defn maybe-on-loaded [context]
  (let [{:keys [state]} context]
    (let [{:keys [tags attached-tags]} state]
      (when (and tags attached-tags)
        ((:on-loaded context) tags attached-tags)))))

(defn run [context]
  (datum.tag.api/tags
   (fn [tags]
     (update-state context #(assoc % :tags tags))
     (get-context context maybe-on-loaded)))
  (datum.album.api/tags (:album-id context)
   (fn [tags]
     (update-state context #(assoc % :attached-tags tags))
     (get-context context maybe-on-loaded))))
