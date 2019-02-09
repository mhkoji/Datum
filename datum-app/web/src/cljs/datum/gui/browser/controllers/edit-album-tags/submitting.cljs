(ns datum.gui.browser.controllers.edit-album-tags.submitting
  (:require [datum.album.api :as api]))

(defn create-store [update-store tags attached-tags on-finished]
  {:state
   {:tags          tags
    :attached-tags attached-tags}

   :submit
   #(api/put-tags album-id attached-tags on-finished)
   })

