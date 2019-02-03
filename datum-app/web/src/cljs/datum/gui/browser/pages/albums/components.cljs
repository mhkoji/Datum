(ns datum.gui.browser.pages.albums.components
  (:require [reagent.core :as r]
            [datum.gui.browser.url :as url]
            [datum.gui.browser.components.header.reagent
             :refer [header-component]]))

(defn rows [num% items item-key item-render]
  (let [count (count items)]
    (let [num (min num% count)
          group (group-by first
                 (map-indexed (fn [index item]
                                (list (quot index num) item))
                              items))]
      [:div {:class "container"}
       (for [row-index (sort < (keys group))]
         ^{:key (str row-index)}
         [:div {:class "card-deck"}
          (for [[_ item] (group row-index)]
            ^{:key (item-key item)}
            [item-render item])])])))


(defn tag-button [{:keys [on-click]}]
  [:button {:type "button" :class "btn" :on-click on-click}
   "Tags"])

(defn cover-component [{:keys [cover on-click-tag-button]}]
  (let [link (url/album (-> cover :album-id))]
    [:div {:class "card mb-4 box-shadow" :style {:maxWidth "18rem"}}
     [:a {:href link}
      [:img {:src (url/image (-> cover :thumbnail))
             :class "card-img-top"}]]

     [:div {:class "card-body"}
      [:div {:class "card-title"} (-> cover :name)]
      (when on-click-tag-button
        [:p [tag-button {:on-click on-click-tag-button}]])]]))


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


(defn page [{:keys [header pager show-covers edit-content-tags]}]
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

         [rows 4 (-> show-covers :state :covers) :album-id
          #(cover-component
            {:cover %
             :on-click-tag-button (-> edit-content-tags :execute)})]

         [pager-component pager]]]])}))
