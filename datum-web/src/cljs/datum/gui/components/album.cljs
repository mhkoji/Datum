(ns datum.gui.components.album
  (:require [reagent.core :as r]
            [datum.gui.components.tag :as tag]
            [datum.gui.components.cards :as cards]
            [datum.gui.url :as url]))

(defn cover-component [{:keys [cover on-click-tag-button]}]
  (let [album-id (-> cover :album-id)
        link     (url/album album-id)]
    [:div {:class "card mb-4 box-shadow"}
     [:a {:href link :target "_blank"}
      [:img {:src (url/image (-> cover :thumbnail))
             :class "card-img-top"}]]

     [:div {:class "card-body"}
      [:div {:class "card-title"}
       [:div {:style {:overflow "hidden"
                      :whiteSpace "nowrap"
                      :textOverflow "ellipsis"}
              :title (-> cover :name)}
        (-> cover :name)]]
      [:div {:class "btn-toolbar" :role "toolbar"}
       [:div
        [tag/button
         {:on-click
          (when on-click-tag-button
            #(on-click-tag-button album-id))}]]]]]))

(defn placeholder-cover-component [_]
  (let [title "..."]
    [:div {:class "card mb-4 box-shadow"}
     [:svg {:class "bd-placeholder-img card-img-top"
            :width "100%"
            :height "180"
            :preserveAspectRatio "xMidYMid slice"
            :focusable :false
            :role "img"}
      ;[:rect {:width "100%" :height "100%" :fill "#868e96"}]
      ]
     [:div {:class "card-body"}
      [:div {:class "card-title"}
       [:div {:style {:overflow "hidden"
                      :whiteSpace "nowrap"
                      :textOverflow "ellipsis"}
              :title title}
        title]]
      [:div {:class "btn-toolbar" :role "toolbar"}
       [:div
        [tag/button {:on-click nil}]]]]]))

(defn covers-component [covers on-click-tag-button]
  [cards/card-decks
   (map (fn [cover]
          {:key   (-> cover :album-id)
           :cover cover
           :on-click-tag-button on-click-tag-button})
        covers)
   :key 4 cover-component])

(defn placeholder-covers-component [{:keys [num]}]
  [cards/card-decks
   (map (fn [i] {:key i}) (range num))
   :key 4 placeholder-cover-component])
