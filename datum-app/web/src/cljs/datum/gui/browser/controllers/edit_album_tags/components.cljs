(ns datum.gui.browser.controllers.edit-album-tags.components
  (:require [reagent.core :as r]
            [cljsjs.react-modal]))

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

(defn loading-component [{:keys [load-tags on-load-tags]}]
  (r/create-class
   {:component-did-mount
    (fn [_] (load-tags))

    :component-did-update
    (fn [comp]
      (let [{:keys [tags attached-tags]} (:state (r/props comp))]
        (when (and tags attached-tags)
          (on-load-tags tags attached-tags))))

    :reagent-render
    (fn []
      [:div "Loading..."])}))

(defn loading-modal [store]
  (r/create-element
   js/ReactModal
   ;; TODO: Should handle close request while loading?
   #js {:isOpen true
        :contentLabel "Tags"}
   (r/as-element
    [:div
     [loading-component store]
     [modal-footer {}]])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn editing-modal [{:keys [on-save on-cancel tag-states]}]
  (r/create-element
   js/ReactModal
   #js {:isOpen true
        :contentLabel "Tags"
        :onRequestClose on-cancel}
   (r/as-element
    [:div
     [:div
      [:div {:class "input-group"}
       [:input {:type "text"
                :class "form-control"
                :value ""
                :on-change #(js/console.log
                             (.-value (.-target %)))}]
       [:div {:class "input-group-append"}
        [:button {:type "button"
                  :class"btn btn-primary"
                  :on-click nil}
         [:span {:class "oi oi-plus"}]]]]

      [:ul {:class "list-group"}
       (for [{:keys [tag attached-p
                     on-toggle on-delete]} tag-states]
         ^{:key (:tag-id tag)}
         [:li {:class "list-group-item"}
          [:label
           [:input {:type "checkbox"
                    :checked attached-p
                    :on-change #(on-toggle tag)}]
           (:name tag)]
          [:div {:class "float-right"}
           [:button {:type "button" :class "btn btn-danger btn-sm"
                     :on-click on-delete}
            [:span {:class "oi oi-delete"}]]]])]]

    [modal-footer {:on-save on-save :on-cancel on-cancel}]])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn saving-component [{:keys [submit]}]
  (r/create-class
   {:component-did-mount
    (fn [_] (submit))

    :reagent-render
    (fn []
      [:div "Saving..."])}))

(defn saving-modal [store]
  (r/create-element
   js/ReactModal
   ;; TODO: Should handle close request while saving?
   #js {:isOpen true
        :contentLabel "Tags"}
   (r/as-element
    [:div
     [saving-component store]
     [modal-footer {}]])))
