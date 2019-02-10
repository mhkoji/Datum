(ns datum.gui.browser.controllers.edit-album-tags.loading
  (:require [datum.tag.api]
            [datum.album.api]))

(defn create-store [update-store album-id on-loaded]
  {:state {:tags          nil
           :attached-tags nil}
   :load-tags
   (fn []
     (datum.tag.api/tags (fn [tags]
      (update-store #(assoc-in % [:state :tags] tags))))

     (datum.album.api/tags album-id (fn [tags]
      (update-store #(assoc-in % [:state :attached-tags] tags)))))

   :on-load-tags on-loaded
   })
