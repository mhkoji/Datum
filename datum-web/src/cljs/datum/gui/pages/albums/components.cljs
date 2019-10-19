(ns datum.gui.pages.albums.components
  (:require [reagent.core :as r]
            [datum.gui.components.loading :refer [spinner]]
            [datum.gui.components.header.reagent :refer [header-component]]
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

(defn album-search-component [{:keys [keyword
                                      on-change-keyword
                                      on-search] :as self}]
  [:input {:class "form-control"
           :value keyword
           :on-change #(on-change-keyword (.-value (.-target %)))
           :on-key-down (fn [evt]
                          (when (= (.-keyCode evt) 13)
                            (on-search self)))}])


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
       [header-component header]

       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Albums"]

        ;; covers
        [:main {:class "pt-3 px-4"}

         (let [{:keys [state]} show-album-covers]
           (if (instance? show-album-covers/Fetching state)
             [spinner]
             [:div {:class "container"}

              [album-search-component search-albums]

              (when pager
                [pager-component pager])

              [show-album-covers/component
               state #(edit-album-tags/start edit-album-tags %)]

              (when pager
                [pager-component pager])
              ]))]]

       [edit-album-tags/modal edit-album-tags]])
    }))
