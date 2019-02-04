(ns datum.gui.browser.pages.album.view.single
  (:require [reagent.core :as r]
            [datum.album.api]
            [datum.gui.browser.components.header.state :as header]
            [datum.album.show-viewer]
            [datum.gui.browser.pages.album.viewer.components]
            [datum.gui.browser.url :as url]
            [datum.gui.browser.util :as util]))

(defn viewer-store [update-store album-id]
  {:type :viewer
   :store (datum.gui.browser.pages.album.view.single/create-store
           (fn [f] (update-store #(update % :store f)))
           images)})

(defn loading-store [update-store images]
  {:type :loading
   :store (datum.gui.browser.pages.album.view.loading/create-store
           (fn [f] (update-store #(update % :store f)))
           album-id
           (fn [images]
             (update-store #(viewer-store update-store pimages))))})


(defn get-uri []
  (goog.Uri.
   (let [hash (.-hash js/location)]
     (if (= hash "") "" (subs hash 1)))))

(defn render-loop-internal [elem album-id]
  (let [renderers
        {:viewer
         (datum.gui.browser.pages.album.view.single/create-renderer elem album-id)
         :loading
         (datum.gui.browser.pages.album.view.loadgin/create-renderer elem)}]
    (util/render-loop {:create-store
                       (fn [update-store]
                         (loading-store update-store album-id))
                       :render
                       (fn [store]
                         ((renderers (:type store))))})))


(defn render-loop [elem {:keys [album-id]}]
  (let [history (goog.History.)]
    (gevents/listen history
                    goog.History/EventType.NAVIGATE
                    #(render-loop-internal elem album-id))
    (.setEnabled history true))
  (render-loop-internal elem album-id))
