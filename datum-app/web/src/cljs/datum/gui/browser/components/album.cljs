(ns datum.gui.browser.components.album
  (:require [reagent.core :as r]
            [datum.gui.components.tag :as tag]
            [datum.gui.components.cards :as cards]
            [datum.gui.browser.url :as url]))

(defn cover-component [{:keys [cover on-click-tag-button]}]
  (let [album-id (-> cover :album-id)
        link     (url/album album-id)]
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
      [:div
       [:p [tag/button
            (if on-click-tag-button
              {:on-click #(on-click-tag-button album-id)}
              {:disabled true})]]]]]))

(defn covers-component [covers on-click-tag-button]
  [cards/card-decks
   (map (fn [cover]
          {:key   (-> cover :album-id)
           :cover cover
           :on-click-tag-button on-click-tag-button})
        covers)
   :key 4 cover-component])
