(ns datum.gui.pages.tag
  (:require [reagent.core :as r]
            [datum.api.tag]
            [datum.gui.controllers.show-tags :as show-tags]
            [datum.gui.controllers.show-album-covers :as show-album-covers]
            [datum.gui.components.header.state :as header]
            [datum.gui.components.header.reagent :refer [header-component]]
            [datum.gui.pages.util :as util]))

(defn ops-component [{:keys [on-edit on-delete]}]
  [:div {:class "btn-toolbar mb-2 mb-md-0"}
   [:div {:class "btn-group mr-2"}
    [:button {:class "btn btn-default btn-sm"
              :on-click on-edit}
     [:span {:class "oi oi-pencil" :aria-hidden "true"}]]]
   [:div {:class "btn-group mr-2"}
    [:button {:class "btn btn-danger btn-sm"
              :on-click on-delete}
     [:span {:class "oi oi-trash" :aria-hidden "true"}]]]])


(defn page [{:keys [show-tags show-album-covers]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      (show-tags/run show-tags)
      (show-album-covers/run show-album-covers))

    :reagent-render
    (fn [{:keys [header show-tags show-album-covers]}]
      [:div
       ;; header
       [header-component header]

       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Tags"]

        [show-tags/component show-tags]

        ;; covers
        [:main {:class "pt-3 px-4"}
         [:div {:class "container"}
          [show-album-covers/component show-album-covers nil]]]]])}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-store [update! tag-id]
  {:header
   (header/get-state :tag)

   :show-tags
   (show-tags/->Context
    (show-tags/->StateContainer
     nil
     (fn [f]
       (update!
        #(update-in % [:show-tags :state-container :state] f))))
    (reify show-tags/Api
      (show-tags/tags [_ k]
        (datum.api.tag/tags k)))
    tag-id)

   :show-album-covers
   (show-album-covers/->Context
    (show-album-covers/->StateContainer
     nil
     (fn [f]
       (update!
        #(update-in % [:show-album-covers :state-container :state] f))))
    (reify show-album-covers/Api
      (show-album-covers/covers [_ k]
        (datum.api.tag/albums tag-id k))))})


(defn render [store elem]
  (r/render [page store] elem))

(defn render-loop [elem {:keys [tag-id]}]
  (util/render-loop {:create-store #(create-store % tag-id)
                     :render       #(render % elem)}))
