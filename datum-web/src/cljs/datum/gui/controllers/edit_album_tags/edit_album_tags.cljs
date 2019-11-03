(ns datum.gui.controllers.edit-album-tags
  (:require [reagent.core :as r]
            [cljsjs.react-modal]
            [datum.tag :as tag]
            [datum.gui.controllers.edit-album-tags.loading :as loading]
            [datum.gui.controllers.edit-album-tags.editing :as editing]
            [datum.gui.controllers.edit-album-tags.saving :as saving]))

(defrecord ClosedContext [update-context])

(defn saving-context [update-context album-id attached-tags]
  (saving/->Context

   album-id

   attached-tags

   (fn []
     (update-context #(->ClosedContext update-context)))))

(defn editing-context [update-context album-id tags attached-tags]
  (editing/->Context

   (editing/->StateContainer
    (editing/->State tags (tag/->AttachedTagSet attached-tags) "")
    (fn [f]
      (update-context #(update-in % [:state-container :state] f))))

   (fn [attached-tags]
     (update-context #(saving-context update-context album-id attached-tags)))

   (fn []
     (update-context #(->ClosedContext update-context)))))

(defn loading-context [update-context album-id]
  (loading/->Context

   album-id

   (loading/->StateContainer
    (loading/->State nil nil)
    (fn [f]
      (update-context #(update-in % [:state-container :state] f)))
    (fn [f]
      (letfn [(update-without-modification [state]
                (f state)
                state)]
        (update-context #(update-in % [:state-container :state]
                                    update-without-modification)))))

   (fn [tags attached-tags]
     (update-context #(editing-context update-context
                                       album-id tags attached-tags)))))


(defn start [context album-id]
  (when (= (type context) ClosedContext)
    (let [update-context (:update-context context)]
      (update-context #(loading-context update-context album-id)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn modal-footer [{:keys [on-cancel on-save]}]
  [:div {:class "modal-footer"}
   [:div {:class "form-row align-items-center"}
    [:div {:class "col-auto"}
     [:button {:class "btn"
               :on-click on-cancel
               :disabled (not on-cancel)}
      "Cancel"]]

    [:div {:class "col-auto"}
     [:button {:class "btn btn-primary"
               :on-click on-save
               :disabled (not on-save)}
      "Save"]]]])

(defmulti modal
  (fn [context] (type context)))

(defmethod modal ClosedContext [context]
  nil)

(defmethod modal loading/Context [context]
  (r/create-element
   js/ReactModal
   ;; TODO: Should handle close request while loading?
   #js {:isOpen true
        :contentLabel "Tags"}
   (r/as-element
    [:div
     [loading/component context]])))

(defmethod modal editing/Context [context]
  (r/create-element
   js/ReactModal
   #js {:isOpen true
        :contentLabel "Tags"
        :onRequestClose #(editing/cancel context)}
   (r/as-element
    [:div
     [editing/component context]
     [modal-footer {:on-save #(editing/save context)
                    :on-cancel #(editing/cancel context)}]])))


(defmethod modal saving/Context [context]
  (r/create-element
   js/ReactModal
   ;; TODO: Should handle close request while saving?
   #js {:isOpen true
        :contentLabel "Tags"}
   (r/as-element
    [:div
     [saving/component context]])))
