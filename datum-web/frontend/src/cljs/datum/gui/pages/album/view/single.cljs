(ns datum.gui.pages.album.view.single
  (:require [goog.events :as gevents]
            [reagent.core :as r]
            [datum.gui.url :as url]
            [datum.gui.components.viewer.single :refer [viewer-component]]))

(defrecord State [images index size])

(defrecord Context [state update-context album-id])

(defn update-state [context f]
  (let [update-context (:update-context context)]
    (update-context #(update % :state f))))

(defn increment-index [context diff]
  (update-state context (fn [state]
    (let [images (-> state :images)
          added-index (+ (-> state :index) diff)
          max-index (dec (count images))
          new-index (cond (< added-index 0) 0
                          (< max-index added-index) max-index
                          :else added-index)]
      (assoc state :index new-index)))))

(defn set-size [context w h]
  (update-state context #(assoc % :size {:width w :height h})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

(defn page [context]
  (letfn [(handle-resize-window []
            (let [w (.-innerWidth js/window)
                  h (.-innerHeight js/window)]
              (set-size context w h)))]
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
      (fn [context]
        (let [{:keys [state album-id]} context
              {:keys [images index size]} state
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

            :on-diff #(increment-index context %)}]))})))
