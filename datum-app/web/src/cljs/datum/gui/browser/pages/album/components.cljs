(ns datum.gui.browser.pages.album.components
  (:require [reagent.core :as r]
            [datum.gui.components.tag :as tag]
            [datum.gui.components.cards :as cards]
            [datum.gui.browser.components.header.reagent
             :refer [header-component]]
            [datum.gui.browser.url :as url]))

(defn page [{:keys [header show-overview]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      ((-> show-overview :execute)))

    :reagent-render
    (fn [{:keys [header show-overview]}]
      [:div
       [header-component header]

       (let [overview (-> show-overview :state :overview)]
         [:main {:class "pt-3 px-4"}
          [:h1 {:class "h2"}
           (or (-> overview :name)
               (-> overview :album-id)
               "Album")]

          (if-let [pictures (-> overview :pictures)]
            [:container
             [:div
              [:p [tag/button {:on-click nil}]]]


             (cards/card-decks 4 pictures :image-id
              (fn [pic]
                [:div {:class "col-md-4"}
                 [:div {:class "card mb-4 box-shadow"}
                  [:a "" ;; TODO
                   [:img {:class "card-img-top"
                          :src (url/image pic)}]]]]))]
            [:div "Loading..."])])])}))
