(ns datum.gui.browser.controllers.edit-album-tags
  (:require [datum.tag]
            [datum.tag.api]
            [datum.album.api]))

(defn loading-store [update-store on-loaded]
  {:type :loading
   :store
   {:state {:tags          nil
            :attached-tags nil}
    :load-tags
    (fn []
      (datum.tag.api/tags
       (fn [tags]
         (update-store
          #(assoc-in % [:store :loading-tags :state :tags]
                     tags))))
      (datum.album.api/tags
       (fn [attached-tags]
         (update-store
          #(assoc-in % [:store :loading-tags :state :attach-tags]
                     attached-tags)))))
    :on-loaded-tags
    (fn [state]
      (on-loaded (-> state :tags)
                 (-> state :attach-tags)))
    }})

(defn editing-store [update-store tags attached-tags
                     on-submitted on-cancelled]
  {:type :editing
   :store
   {:state
    {:tags             tags
     :attached-tag-set (datum.tag/AttachedTagSet. attached-tags)}

    :attach
    (fn [tag]
      (update-store
       #(update-in % [:store :state :attached-tag-set]
                   datum.tag/attach tag)))

    :detach
    (fn [tag]
      (update-store
       #(update-in % [:store :state :attached-tag-set]
                   datum.tag/detach tag)))

    :submit
    (fn [state]
      (on-submitted (-> state :tags)
                    (-> state :attached-tag-set :tags)))

    :cancel
    on-cancelled
    }})

(defn submitting-store [update-store tags attached-tags on-finished]
  {:type :submitting
   :store
   {:state
    {:tags          tags
     :attached-tags attached-tags}

    {:submit
     (fn []
       (datum.album.api/put-tags album-id attached-tags on-finished))
     }}})
