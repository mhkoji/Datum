(ns datum.gui.controllers.edit-album-tags.editing
  (:require [datum.tag :as tag]
            [datum.tag.api]))

(defrecord State [tags attached-tag-set new-name])

(defrecord StateContainer [state update])

(defrecord Context [state-container on-save on-cancel])

(defn update-state [context f]
  ((-> context :state-container :update) f))

(defn refresh-tags [context]
  (datum.tag.api/tags
   (fn [tags] (update-state context #(assoc % :tags tags)))))

(defn attach-tag [context tag]
  (update-state context #(update % :attached-tag-set tag/attach tag)))

(defn detach-tag [context tag]
  (update-state context #(update % :attached-tag-set tag/detach tag)))

(defn delete-tag [context tag]
  (detach-tag context tag)
  (datum.tag.api/delete-tag tag #(refresh-tags context)))


(defn change-name [context name]
  (update-state context #(assoc % :new-name name)))

(defn add-tag [context]
  (let [name (-> context :state-container :state :new-name)]
    (datum.tag.api/put-tags name
     (fn [_]
       (update-state context #(assoc % :new-name ""))
       (refresh-tags context)))))


(defn save [context]
  (let [{:keys [on-save state-container]} context]
    (let [{:keys [state]} state-container]
      (on-save (tag/attached-tags (-> state :attached-tag-set))))))

(defn cancel [context]
  ((:on-cancel context)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn component [context]
  [:div
   (let [{:keys [new-name]}
         (-> context :state-container :state)]
     [:div {:class "input-group"}
      [:input {:type "text"
               :class "form-control"
               :value new-name
               :on-change #(change-name context (.-value (.-target %)))}]
      [:div {:class "input-group-append"}
       [:button {:type "button"
                 :class"btn btn-primary"
                 :on-click #(add-tag context)}
        [:span {:class "oi oi-plus"}]]]])
   [:ul {:class "list-group"}
    (let [{:keys [tags attached-tag-set]}
          (-> context :state-container :state)]
      (for [tag tags]
        (let [attached-p (datum.tag/attached-p attached-tag-set tag)
              on-toggle  (if attached-p
                           #(detach-tag context tag)
                           #(attach-tag context tag))
              on-delete  #(delete-tag context tag)]
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
             [:span {:class "oi oi-delete"}]]]])))]])
