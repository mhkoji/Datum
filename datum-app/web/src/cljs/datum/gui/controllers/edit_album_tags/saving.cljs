(ns datum.gui.controllers.edit-album-tags.saving
  (:require [datum.album.api :as api]))

(defn create-store [update-store album-id attached-tags on-finished]
  {:submit #(api/put-tags album-id attached-tags on-finished)
   })
