(ns datum.gui.pages.album.components
  (:require [reagent.core :as r]
            [datum.gui.controllers.show-album-overview
             :as show-album-overview]
            [datum.gui.controllers.edit-album-tags :as edit-album-tags]
            [datum.gui.components.tag :as tag]
            [datum.gui.components.cards :as cards]
            [datum.gui.components.header.reagent
             :refer [header-component]]
            [datum.gui.url :as url]))

(defn image-component [{:keys [image album-id]}]
  [:div {:class "col-md-4"}
   [:div {:class "card mb-4 box-shadow"}
    [:a {:href (url/album-viewer-single album-id image)}
     [:img {:class "card-img-top"
            :src (url/image image)}]]]])

(defn page [{:keys [header show-album-overview]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      (show-album-overview/run show-album-overview))

    :reagent-render
    (fn [{:keys [header show-album-overview edit-album-tags]}]
      [:div
       [header-component header]

       (let [overview (-> show-album-overview :state :overview)
             album-id (-> overview :album-id)]
         [:main {:class "pt-3 px-4"}
          [:h1 {:class "h2"}
           (or (-> overview :name) album-id "Album")]

          (if-let [pictures (-> overview :pictures)]
            [:container
             [:div
              [edit-album-tags/modal edit-album-tags]
              [:p [tag/button {:on-click
                               #(edit-album-tags/start
                                 edit-album-tags album-id)}]]]

             [cards/card-decks
              (map (fn [image]
                     {:key      (:image-id image)
                      :album-id album-id
                      :image    image})
                   pictures)
              :key 4 image-component]]
            [:div "Loading..."])])])}))
