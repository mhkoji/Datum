(ns datum.gui.controllers.edit-album-tags.editing
  (:require [datum.tag :as tag]
            [datum.tag.api]))

(defn create-store [update-store
                    tags attached-tags
                    on-saved on-cancelled]
  {:existing
   {:state
    {:tags             tags
     :attached-tag-set (tag/AttachedTagSet. attached-tags)}

    :attach
    (fn [tag]
      (update-store #(update-in % [:existing :state :attached-tag-set]
                                tag/attach tag)))

    :detach
    (fn [tag]
      (update-store #(update-in % [:existing :state :attached-tag-set]
                                tag/detach tag)))

    :save
    (fn [state]
      (on-saved (-> state :attached-tag-set :tags)))

    :cancel
    on-cancelled


    :delete
    (fn [existing tag]
      ((-> existing :detach) tag)
      (datum.tag.api/delete-tag tag (fn []
        (datum.tag.api/tags (fn [tags]
          (update-store #(assoc-in % [:existing :state :tags] tags)))))))
    }

   :new
   {:name ""

    :change
    (fn [name]
      (update-store #(assoc-in % [:new :name] name)))

    :create
    (fn [name]
      (update-store #(assoc-in % [:new :name] ""))
      (datum.tag.api/put-tags name (fn []
        (datum.tag.api/tags (fn [tags]
          (update-store #(assoc-in % [:existing :state :tags] tags)))))))
    }
   })
