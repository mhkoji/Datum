(ns datum.gui.pages.albums.components
  (:require [reagent.core :as r]
            [datum.gui.components.header.reagent :refer [header-component]]
            [datum.gui.controllers.search-albums :as search-albums]
            [datum.gui.controllers.show-album-covers :as show-album-covers]
            [datum.gui.controllers.edit-album-tags :as edit-album-tags]))

(defn link [{:keys [link enabled]} & children]
  [:a {:href link
       :class (str "btn" (if enabled "" " disabled"))}
   children])

(defn icon-prev []
  [:span {:key "left" :class "oi oi-chevron-left"}])

(defn icon-next []
  [:span {:key "right" :class "oi oi-chevron-right"}])

(defn pager-component [{:keys [prev next]}]
  [:div {:class "btn-toolbar" :role "toolbar"}
   [link prev ^{:key "prev"} [icon-prev]]
   [link next ^{:key "next"} [icon-next]]])


(defn page [{:keys [header pager
                    search-albums
                    show-album-covers
                    edit-album-tags]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      (show-album-covers/run show-album-covers))

    :reagent-render
    (fn [{:keys [header pager
                 search-albums
                 show-album-covers
                 edit-album-tags]}]
      [:div
       ;; header
       [header-component header
        {:form [search-albums/header-form search-albums]}]

       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Albums"]

        ;; covers
        [:main {:class "pt-3 px-4"}

         [:div {:class "container"}
          (when pager
            [pager-component pager])

          [show-album-covers/component
           show-album-covers
           #(edit-album-tags/start edit-album-tags %)]

          (when pager
            [pager-component pager])
          ]]]

       [edit-album-tags/modal edit-album-tags]])
    }))
