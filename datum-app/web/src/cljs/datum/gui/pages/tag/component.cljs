(ns datum.gui.pages.tag.components
  (:require [reagent.core :as r]
            [datum.gui.components.cards :as cards]
            [datum.gui.components.tag :as tag]
            [datum.gui.components.header.reagent
             :refer [header-component]]
            [datum.gui.url :as url]))

(defn cover-component [cover]
  (let [link (url/album (-> cover :album-id))]
    [:div {:class "card mb-4 box-shadow" :style {:maxWidth "18rem"}}
     [:a {:href link}
      [:img {:src (url/image (-> cover :thumbnail))
             :class "card-img-top"}]]

     [:div {:class "card-body"}
      [:div {:class "card-title"} (-> cover :name)]
      [:a {:href link
           :class "btn btn-secondary btn-sm"
           :type "button"}
       "Open"]]]))

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


(defn page [{:keys [tag-id show-tags show-covers]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      ((-> show-tags :execute) tag-id)
      ((-> show-covers :execute) tag-id))

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
