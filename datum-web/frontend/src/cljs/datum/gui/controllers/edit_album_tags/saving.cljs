(ns datum.gui.controllers.edit-album-tags.saving
  (:require [reagent.core :as r]
            [datum.gui.components.loading :refer [spinner]]
            [cljs.core.async :refer [go <! timeout]]
            [datum.album.api]))

(defrecord Context [album-id attached-tags on-saved])

(defn on-saved [context]
  ((:on-saved context)))

(defn run [context]
  (datum.album.api/put-tags (:album-id context)
                            (:attached-tags context)
   (fn []
     (go
       (<! (timeout 100))
       (on-saved context)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn component [context]
  (r/create-class
   {:component-did-mount
    (fn [_]
      (run context))

    :reagent-render
    (fn []
      [spinner])}))
