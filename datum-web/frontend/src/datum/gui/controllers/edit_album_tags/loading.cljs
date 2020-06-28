(ns datum.gui.controllers.edit-album-tags.loading
  (:require [reagent.core :as r]
            [datum.gui.components.loading :refer [spinner]]
            [datum.api.tag]
            [datum.api.album]))

(defrecord State [tags attached-tags])

(defrecord StateContainer [state update get])

(defrecord Context [album-id state-container on-loaded])

(defn update-state [context f]
  ((-> context :state-container :update) f))

(defn get-state [context f]
  ((-> context :state-container :get) f))

(defn maybe-on-loaded [context]
  (get-state context
   (fn [state]
     (let [{:keys [tags attached-tags]} state]
       (when (and tags attached-tags)
         ((:on-loaded context) tags attached-tags))))))

(defn run [context]
  (datum.api.tag/tags
   (fn [tags]
     (update-state context #(assoc % :tags tags))
     (maybe-on-loaded context)))
  (datum.api.album/tags (:album-id context)
   (fn [tags]
     (update-state context #(assoc % :attached-tags tags))
     (maybe-on-loaded context))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn component [loading-context]
  (r/create-class
   {:component-did-mount
    (fn [_]
      (run loading-context))

    :reagent-render
    (fn []
      [spinner])}))
