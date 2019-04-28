(ns datum.gui.pages.albums.components
  (:require [reagent.core :as r]
            [datum.gui.components.album
             :as album-components]
            [datum.gui.components.loading
             :as loading-components]
            [datum.gui.components.header.reagent
             :refer [header-component]]
            [datum.gui.controllers.show-album-covers
             :as show-album-covers]
            [datum.gui.controllers.edit-album-tags
             :as edit-album-tags]))

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


(defn page [{:keys [header pager
                    show-album-covers
                    edit-album-tags]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      (show-album-covers/run (-> show-album-covers :transaction)
                             (-> show-album-covers :api)))

    :reagent-render
    (fn [{:keys [header pager show-album-covers edit-album-tags]}]
      [:div
       ;; header
       [header-component header]

       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Albums"]

        [edit-album-tags/component edit-album-tags]

        ;; covers
        [:main {:class "pt-3 px-4"}

         (let [{:keys [type covers]} (-> show-album-covers :state)]
           (if (= type :loading)
             "Loading..."
             [:div
              [pager-component pager]

              (cond (= type :appending)
                    [album-components/covers-component covers nil]

                    (= type :finished)
                    (if (empty? covers)
                      "EMPTY!"
                      [album-components/covers-component
                       covers
                       #(edit-album-tags/start edit-album-tags %)]))

              [pager-component pager]]))]]])
    }))
