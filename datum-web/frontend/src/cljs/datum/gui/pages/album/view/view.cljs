(ns datum.gui.pages.album.view
  (:require [goog.events :as gevents]
            [goog.History]
            [goog.Uri]
            [reagent.core :as r]
            [datum.gui.pages.album.view.single :as single]
            [datum.gui.pages.album.view.loading :as loading]
            [datum.gui.url :as url]
            [datum.gui.pages.util :as util]))

(defn get-initial-image-id []
  (let [uri (goog.Uri. js/location.search)]
    (-> uri (.getQueryData) (.get "current"))))

(defn single-context [update-store album-id images index]
  (let [state (single/State. images index nil)]
    (single/Context. state update-store album-id)))

(defn loading-context [update-store album-id]
  (letfn [(handle-on-loaded [images]
            (let [index-or-null
                  (when-let [initial-image-id (get-initial-image-id)]
                    (.indexOf (map :image-id images) initial-image-id))]
              (update-store #(single-context update-store
                                             album-id
                                             images
                                             (or index-or-null 0)))))]
    (loading/Context. nil update-store album-id handle-on-loaded)))


(defmulti render
  (fn [context elem] (type context)))

(defmethod render single/Context [context elem]
  (r/render [single/page context] elem))

(defmethod render loading/Context [context elem]
  (r/render [loading/page context] elem))

(defn get-uri [] ;; for SPA
  (goog.Uri.
   (let [hash (.-hash js/location)]
     (if (= hash "") "" (subs hash 1)))))

(defn render-loop-internal [elem album-id]
  (util/render-loop {:create-store #(loading-context % album-id)
                     :render #(render % elem)}))

(defn render-loop [elem {:keys [album-id]}]
  (let [history (goog.History.)]
    (gevents/listen history
                    goog.History/EventType.NAVIGATE
                    #(render-loop-internal elem album-id))
    (.setEnabled history true))
  (render-loop-internal elem album-id))
