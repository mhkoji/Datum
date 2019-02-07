(ns datum.gui.browser.pages.album.view
  (:require [goog.events :as gevents]
            [goog.History]
            [reagent.core :as r]
            [datum.album.api]
            [datum.gui.browser.pages.album.view.single :as single]
            [datum.gui.browser.pages.album.view.loading :as loading]
            [datum.gui.browser.url :as url]
            [datum.gui.browser.util :as util]))

(defn viewer-store [update-store images]
  {:type :viewer
   :store (single/create-store
           (fn [f] (update-store #(update % :store f)))
           images)})

(defn loading-store [update-store album-id]
  {:type :loading
   :store (loading/create-store
           (fn [f] (update-store #(update % :store f)))
           album-id
           (fn [images]
             (update-store #(viewer-store update-store images))))
   })


(defn get-uri []
  (goog.Uri.
   (let [hash (.-hash js/location)]
     (if (= hash "") "" (subs hash 1)))))

(defn render-loop-internal [elem album-id]
  (let [renderers {:viewer (single/create-renderer elem album-id)
                   :loading (loading/create-renderer elem)}]
    (util/render-loop {:create-store
                       (fn [update-store]
                         (loading-store update-store album-id))
                       :render
                       (fn [store]
                         (let [render (renderers (:type store))]
                           (render (:store store))))
                       })))


(defn render-loop [elem {:keys [album-id]}]
  (let [history (goog.History.)]
    (gevents/listen history
                    goog.History/EventType.NAVIGATE
                    #(render-loop-internal elem album-id))
    (.setEnabled history true))
  (render-loop-internal elem album-id))
