(ns datum.gui.browser.pages.album.view.single.components
  (:require [goog.events :as gevents]
            [reagent.core :as r]
            [datum.gui.browser.url :as url]
            [datum.gui.browser.components.viewer.single
             :refer [viewer-component]]))

(defn neighboring-images [images index width]
  (let [count (count images)
        half-width (/ width 2)]
    (cond (<= count width)
          (take count images)

          (< index half-width)
          (take width images)

          (< count (+ index (- half-width) width))
          (take width (drop (- count width) images))

          :else
          (take width (drop (- index half-width) images)))))

(defn page [{:keys [album-id set-size]}]
  (letfn [(handle-resize-window []
            (set-size {:width (.-innerWidth js/window)
                       :height (.-innerHeight js/window)}))]
    (r/create-class
     {:component-did-mount
      (fn [this]
        (.addEventListener js/window gevents/EventType.RESIZE
                           handle-resize-window)
        (handle-resize-window))

      :component-will-unmount
      (fn [this]
        (.removeEventListener js/window gevents/EventType.RESIZE
                              handle-resize-window))

      :reagent-render
      (fn [{:keys [state increment-index]}]
        (let [{:keys [images index size]} state
              current-image               (nth images index)]
          [viewer-component
           {:current-image current-image

            :neighboring-thumbnails
            (map (fn [image]
                   {:image image
                    :link (url/album-viewer-single album-id image)
                    :highlighted-p (= (:image-id image)
                                      (:image-id current-image))})
                 (neighboring-images images index 3))

            :progress {:now index :max (count images)}

            :size size

            :on-diff increment-index}]))})))
