(ns datum.gui.browser.controllers.edit-album-tags.editing
  (:require [datum.tag :as tag]))

(defn create-store [update-store
                    tags attached-tags
                    on-saved on-cancelled]
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

   :save
   (fn [state]
     (on-saved (-> state :attached-tag-set :tags)))

   :cancel
   on-cancelled
   })
