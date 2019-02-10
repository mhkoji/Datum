(ns datum.gui.browser.pages.albums.components
  (:require [reagent.core :as r]
            [datum.gui.components.tag :as tag]
            [datum.gui.components.cards :as cards]
            [datum.gui.browser.components.header.reagent
             :refer [header-component]]
            [datum.gui.browser.controllers.edit-album-tags
             :as edit-album-tags]
            [datum.gui.browser.url :as url]))

(defn cover-component [{:keys [cover on-click-tag-button]}]
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
       "Open"]
      [:div ]
      (when on-click-tag-button
        [:p [tag/button {:on-click on-click-tag-button}]])]]))


(defn link [{:keys [link enabled]} & children]
  [:a {:href link
       :class (str "btn" (if enabled "" " disabled"))}
   children])

(defn icon-prev []
  [:span {:key "left" :class "oi oi-chevron-left"}])

(defn icon-next []
  [:span {:key "right" :class "oi oi-chevron-right"}])

(defn pager-component [{:keys [prev next]}]
  [:div {:class "container"}
   [:div {:class "btn-toolbar" :role "toolbar"}
    [link prev ^{:key "prev"} [icon-prev]]
    [link next ^{:key "next"} [icon-next]]]])


(defn page [{:keys [header pager show-covers edit-album-tags]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      ((-> show-covers :execute)))

    :reagent-render
    (fn [{:keys [nav show-covers]}]
      [:div
       ;; header
       [header-component header]

       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Albums"]
        ;; covers
        [:main {:class "pt-3 px-4"}
         (when (-> show-covers :state :loading)
           [:div "Loading..."])

         [pager-component pager]

         [cards/card-decks 4 (-> show-covers :state :covers) :album-id
          (fn [cover]
            [cover-component
             {:cover cover
              :on-click-tag-button #((-> edit-album-tags :store :start)
                                     (-> cover :album-id))}])]

         [pager-component pager]

         [edit-album-tags/component edit-album-tags]]]])}))
