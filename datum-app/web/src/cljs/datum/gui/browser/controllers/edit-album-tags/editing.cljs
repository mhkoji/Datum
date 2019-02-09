(ns datum.gui.browser.controllers.edit-album-tags.editing
  (:require [datum.tag :as tag]))

(defn create-store [update-store
                    tags attached-tags
                    on-submitted on-cancelled]
  {:state
   {:tags             tags
    :attached-tag-set (tag/AttachedTagSet. attached-tags)}

   :attach
   (fn [tag]
     (update-store
      #(update-in % [:state :attached-tag-set] tag/attach tag)))

   :detach
   (fn [tag]
     (update-store
      #(update-in % [:state :attached-tag-set] tag/detach tag)))

    :submit
   (fn [state]
     (on-submitted (-> state :tags)
                   (-> state :attached-tag-set :tags)))

   :cancel
   on-cancelled
   })
