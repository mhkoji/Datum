(ns datum.gui.controllers.edit-album-tags.components
  (:require [reagent.core :as r]
            [cljsjs.react-modal]
            [datum.gui.controllers.edit-album-tags.loading :as loading]
            [datum.gui.controllers.edit-album-tags.editing :as editing]
            [datum.gui.controllers.edit-album-tags.saving :as saving]))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn loading-component [loading-context]
  (r/create-class
   {:component-did-mount
    (fn [_]
      (loading/run loading-context))

    :component-did-update
    (fn [comp]
      (let [loading-context (r/props comp)]
        (let [{:keys [tags attached-tags]} (-> loading-context :state)]
          (when (and tags attached-tags)
            (loading/on-loaded loading-context)))))

    :reagent-render
    (fn []
      [:div "Loading..."])}))

(defn loading-modal [loading-context]
  (r/create-element
   js/ReactModal
   ;; TODO: Should handle close request while loading?
   #js {:isOpen true
        :contentLabel "Tags"}
   (r/as-element
    [:div
     [loading-component loading-context]
     [modal-footer {}]])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn editing-modal [editing-context]
  (r/create-element
   js/ReactModal
   #js {:isOpen true
        :contentLabel "Tags"
        :onRequestClose #(editing/cancel editing-context)}
   (r/as-element
    [:div
     [:div
      (let [{:keys [new-name]} (:state editing-context)]
        [:div {:class "input-group"}
         [:input {:type "text"
                  :class "form-control"
                  :value new-name
                  :on-change #(editing/change-name
                               editing-context (.-value (.-target %)))}]
         [:div {:class "input-group-append"}
          [:button {:type "button"
                    :class"btn btn-primary"
                    :on-click #(editing/add-tag editing-context)}
           [:span {:class "oi oi-plus"}]]]])

      [:ul {:class "list-group"}
       (let [{:keys [tags attached-tag-set]} (:state editing-context)]
         (for [tag tags]
           (let [attached-p (datum.tag/attached-p attached-tag-set tag)
                 on-toggle  (if attached-p
                              #(editing/detach-tag editing-context tag)
                              #(editing/attach-tag editing-context tag))
                 on-delete  #(editing/delete-tag editing-context tag)]
             ^{:key (:tag-id tag)}
             [:li {:class "list-group-item"}
              [:label
               [:input {:type "checkbox"
                        :checked attached-p
                        :on-change on-toggle}]
               (:name tag)]
              [:div {:class "float-right"}
               [:button {:type "button"
                         :class "btn btn-danger btn-sm"
                         :on-click on-delete}
                [:span {:class "oi oi-delete"}]]]])))]]

     [modal-footer {:on-save #(editing/save editing-context)
                    :on-cancel #(editing/cancel editing-context)}]])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn saving-component [saving-context]
  (r/create-class
   {:component-did-mount
    (fn [_]
      (saving/run saving-context))

    :component-did-update
    (fn [comp]
      (let [saving-context (r/props comp)]
        (let [{:keys [status]} (-> saving-context :state)]
          (when (= status ::saving/saved)
            (saving/on-saved saving-context)))))

    :reagent-render
    (fn []
      [:div "Saving..."])}))

(defn saving-modal [saving-context]
  (r/create-element
   js/ReactModal
   ;; TODO: Should handle close request while saving?
   #js {:isOpen true
        :contentLabel "Tags"}
   (r/as-element
    [:div
     [saving-component saving-context]
     [modal-footer {}]])))
