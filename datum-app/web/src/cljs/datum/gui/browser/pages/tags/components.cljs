(ns datum.gui.browser.pages.tags.components
  (:require [reagent.core :as r]
            [datum.gui.components.cards :as cards]
            [datum.gui.browser.components.tag :as tag]
            [datum.gui.browser.components.header.reagent
             :refer [header-component]]
            [datum.gui.browser.pages.tag.components
             :refer [cover-component]]
            [datum.gui.browser.url :as url]))

(defn page [{:keys [show-tags show-covers]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      ((-> show-tags :execute))
      ((-> show-covers :execute)))

    :reagent-render
    (fn [{:keys [header show-tags show-covers]}]
      [:div
       ;; header
       [header-component header]

       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Tags"]

        [tag/menu-component {:items (-> show-tags :state :items)}]

        ;; covers
        (let [{:keys [covers loading-p]} (-> show-covers :state)]
          [:main {:class "pt-3 px-4"}

           (if loading-p
             [:div "Loading..."]
             [cards/card-decks covers :album-id 4 cover-component])])
        ]])
    }))
