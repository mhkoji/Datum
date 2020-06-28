(ns datum.gui.components.album
  (:require [reagent.core :as r]
            [datum.gui.components.tag :as tag]
            [datum.gui.components.cards :as cards]
            [datum.gui.url :as url]))

(defn cover-component [{:keys [cover on-click-tag-button]}]
  (let [album-id (-> cover :album-id)
        link     (url/album album-id)]
    [:div {:class "card box-shadow"}
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

(defn covers-component [covers on-click-tag-button]
  [:div {:class "card-columns"}
   (map (fn [cover]
          [cover-component
           {:key   (-> cover :album-id)
            :cover cover
            :on-click-tag-button on-click-tag-button}])
        covers)])
