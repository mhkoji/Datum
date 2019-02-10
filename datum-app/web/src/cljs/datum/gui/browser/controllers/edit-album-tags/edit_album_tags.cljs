(ns datum.gui.browser.controllers.edit-album-tags
  (:require [datum.gui.browser.controllers.edit-album-tags.loading
             :as loading]
            [datum.gui.browser.controllers.edit-album-tags.editing
             :as editing]
            [datum.gui.browser.controllers.edit-album-tags.submitting
             :as submitting]
            [datum.gui.browser.controllers.edit-album-tags.closed
             :as closed]
            [datum.gui.browser.controllers.edit-album-tags.components
             :as components]))

(defn wrap [update-store]
  (fn [f]
    (update-store #(update % :store f))))

(defn loading-store [update-store album-id]
  {:type :loading
   :store (loading/create-store
           (wrap update-store) album-id
           (fn [tags attached-tags]
             (update-store
              #(editing-store update-store tags attached-tags))))
   })

(defn editing-store [update-store tags attached-tags]
  {:type :editting
   :store (editing/create-store
           (wrap update-store) tags attached-tags
           (fn [tags attached-tags]
             (update-store #(submitting-store tags attached-tags)))
           (fn []
             (update-store #(closed-store update-store))))
   })

(defn submitting-store [update-store tags attached-tags]
  {:type :submitting
   :store (submitting/create-store
           (wrap update-store) tags attached-tags
           (fn []
             (update-store #(closed-store update-store))))
   })

(defn closed-store [update-store]
  {:type :closed
   :store (closed/create-store
           (wrap update-store)
           (fn [album-id]
             (update-store #(loadig-store album-id))))
   })


(defmulti component
  (fn [store] (:type store)))


(defmethod component :closed [store]
  nil)

(defmethod component :loading [store]
  [components/loading-modal])


(defmethod component  [store]
  [components/editing-modal (:store store)])


(defmethod component :submitting [store]
  [components/submitting-modal])
