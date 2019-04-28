(ns datum.gui.components.tag
  (:require [datum.gui.url :as url]))

(defn menu-component [{:keys [items]}]
  [:ul {:class "nav nav-tabs"}
   (for [{:keys [id url name selected-p]} items]
     ^{:key id}
     [:li {:class "nav-item"}
      [:a {:class (str "nav-link" (if selected-p " active" ""))
           :href url}
       name]])])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn button [{:keys [on-click]}]
  [:button {:type "button"
            :class "btn btn-outline-secondary"
            :on-click on-click}
   "Tags"])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn name-edit-component-editing [{:keys [name
                                           on-change
                                           on-cancel
                                           on-save]}]
  [:div {:class "input-group"}
   [:input {:type "text"
            :class "form-control"
            :value name
            :on-change #(on-change (.-value (.-target %)))}]
   [:div {:class "input-group-append"}
    [:button {:class "btn btn-outline-secondary"
              :on-click on-cancel}
     [:span {:class "oi oi-circle-x" :aria-hidden "true"}]]
      [:button {:class "btn btn-outline-secondary btn-primary"
                :on-click on-save}
       [:span {:class "oi oi-cloud-upload" :aria-hidden "true"}]]]])

(defn name-edit-component-saving [_]
  [:div "Saving..."])

(defmulti name-edit-component
  (fn [typed-state] (:type typed-state)))

(defmethod name-edit-component :default [typed-state]
  nil)

(defmethod name-edit-component :editing [typed-state]
  [name-edit-component-editing (:state typed-state)])

(defmethod name-edit-component :saving [typed-state]
  [name-edit-component-saving (:state typed-state)])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
