(ns datum.gui.browser.components.viewer.single
  (:require  [goog.events :as gevents]
             [reagent.core :as r]
             [datum.viewer :as viewer]
             [datum.gui.browser.url :as url]
             [datum.gui.browser.components.viewer.util :as util]
             [datum.gui.browser.components.viewer.progress
              :refer [progress-component]]))

(defn resize-viewer! [image-elem thumbnail-elem {:keys [height width]}]
  (when (and image-elem thumbnail-elem)
    (util/set-max-size! image-elem
                        :width (- width (.-offsetWidth thumbnail-elem))
                        :height height)
    (util/set-max-size! thumbnail-elem
                        :height height)))

(defn make-diff [forward-p]
  (if forward-p 1 -1))


(defn viewer-component [{:keys [on-diff]}]
  (let [image-elem     (atom nil)
        thumbnail-elem (atom nil)
        on-keydown     (comp on-diff make-diff util/forward-keydown-p)
        on-wheel       (comp on-diff make-diff util/forward-wheel-p)]
    (r/create-class
     {:component-did-mount
      (fn [comp]
        (resize-viewer! @image-elem @thumbnail-elem
                        (-> (r/props comp) :size))
        (.addEventListener
         js/window gevents/EventType.KEYDOWN on-keydown))

      :component-did-update
      (fn [comp prev-props prev-state]
        (resize-viewer! @image-elem @thumbnail-elem
                        (-> (r/props comp) :size)))

      :component-will-unmount
      (fn [comp]
        (.removeEventListener
         js/window gevents/EventType.KEYDOWN on-keydown))

      :reagent-render
      (fn [{:keys [current-image neighboring-thumbnails progress on-diff]}]
        [:div {:class "datum-component-singleimageviewer"}

         [:div {:class "datum-component-singleimageviewer-left"
                :style {:position "absolute" :float "left"}}
          [:div {:class "datum-component-singleimageviewer-image"}
           [:div {:on-wheel on-wheel :style {:display "inline-block"}}
            [:img {:src (url/image current-image)
                   :ref #(reset! image-elem %)}]]]]

         [:div {:class "datum-component-singleimageviewer-right"}
          [:div {:ref #(reset! thumbnail-elem %)
                 :class "datum-component-standardviewer-thumbnails"}

           (let [base-name "datum-component-standardviewer-thumbnail"]
             (for [{:keys [image link highlighted-p]} neighboring-thumbnails]
               ^{:key (-> image :image-id)}
               [:div {:class (str base-name
                                  (if highlighted-p
                                    (str " " base-name "-highlighted")
                                    ""))}
                [:a {:href link}
                 [:img {:src (url/image image) :class base-name}]]]))

           (progress-component progress)]]])})))
