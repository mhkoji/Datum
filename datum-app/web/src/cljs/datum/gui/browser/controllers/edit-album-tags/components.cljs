(ns datum.gui.browser.controllers.edit-album-tags.components)

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

(defn loading-modal []
  (r/create-element
   js/ReactModal
   #js {:isOpen true
        :contentLabel "Tags"
        :onRequestClose on-cancel}
   (r/as-element
    [:div
     [:div "Loading..."]
     [modal-footer]])))

(defn submitting-modal []
  (r/create-element
   js/ReactModal
   #js {:isOpen true
        :contentLabel "Tags"
        :onRequestClose on-cancel}
   (r/as-element
    [:div
     [:div "Submitting..."]
     [modal-footer]])))

(defn editing-modal []
  (r/create-element
   js/ReactModal
   #js {:isOpen true
        :contentLabel "Tags"
        :onRequestClose on-cancel}
   (r/as-element
    [:div
     [:div
      [:div {:class "input-group"}
       [:input {:type "text" :class "form-control" :value (-> new :name)
                :on-change #((-> new :on-change)
                             (.-value (.-target %)))}]
       [:div {:class "input-group-append"}
        [:button {:type "button" :class"btn btn-primary"
                  :on-click #((-> new :on-add))}
         [:span {:class "oi oi-plus"}]]]]
      [:ul {:class "list-group"}
       (for [tag tags]
         ^{:key (:tag-id tag)}
         [:li {:class "list-group-item"}
          [:label
           [:input {:type "checkbox"
                    :checked (datum.tag/attached-p attached-tag-set tag)
                    :on-change on-toggle}]
           (:name tag)]
          [:div {:class "float-right"}
           [:button {:type "button" :class "btn btn-danger btn-sm"
                     :on-click on-delete}
            [:span {:class "oi oi-delete"}]]]]))]]

    [modal-footer])))
