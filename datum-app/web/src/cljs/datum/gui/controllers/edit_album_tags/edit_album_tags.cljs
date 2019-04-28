(ns datum.gui.controllers.edit-album-tags
  (:require [datum.tag]
            [datum.gui.controllers.edit-album-tags.loading
             :as loading]
            [datum.gui.controllers.edit-album-tags.editing
             :as editing]
            [datum.gui.controllers.edit-album-tags.saving
             :as saving]
            [datum.gui.controllers.edit-album-tags.components
             :as components]))

(defn wrap [update-store]
  (fn [f]
    (update-store #(update % :store f))))

(declare loading-store
         editing-store
         saving-store
         closed-store)

(defn loading-store [update-store album-id]
  {:type :loading
   :store (loading/create-store
           (wrap update-store) album-id
           (fn [tags attached-tags]
             (update-store #(editing-store update-store
                                           album-id
                                           tags
                                           attached-tags))))
   })

(defn editing-store [update-store album-id
                     tags attached-tags]
  {:type :editing
   :store (editing/create-store
           (wrap update-store) tags attached-tags
           (fn [attached-tags]
             (update-store #(saving-store update-store
                                          album-id
                                          attached-tags)))
           (fn []
             (update-store #(closed-store update-store))))
   })

(defn saving-store [update-store album-id attached-tags]
  {:type :saving
   :store (saving/create-store
           (wrap update-store) album-id attached-tags
           (fn []
             (update-store #(closed-store update-store))))
   })

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn closed-store [update-store]
  {:type :closed
   :store {:start
           (fn [album-id]
             (update-store #(loading-store update-store album-id)))}
   })


(defmulti start
  (fn [store album-id] (:type store)))

(defmethod start :closed [store album-id]
  ((:start (:store store)) album-id))

(defmethod start :default [store album-id]
  nil)


(defmulti component
  (fn [store] (:type store)))

(defmethod component :closed [store]
  nil)

(defmethod component :loading [store]
  [components/loading-modal (:store store)])

(defmethod component :editing [store_]
  (let [{:keys [new existing]} (:store store_)]
    [components/editing-modal
     {:new-tag
      {:name      (-> new :name)
       :on-change (-> new :change)
       :on-create #((-> new :create) (-> new :name))
       }

      :existing-tags
      (let [{:keys [tags attached-tag-set]} (-> existing :state)]
        (map (fn [tag]
               (let [attached-p (datum.tag/attached-p attached-tag-set
                                                      tag)]
                 {:tag        tag
                  :attached-p attached-p
                  :on-toggle  (if attached-p
                                (-> existing :detach)
                                (-> existing :attach))
                  :on-delete  #((-> existing :delete) existing %)
                  }))
             tags))

      :on-save
      #((-> existing :save) (-> existing :state))

      :on-cancel
      (-> existing :cancel)
      }]))

(defmethod component :saving [store]
  [components/saving-modal (:store store)])
