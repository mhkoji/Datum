(ns datum.gui.pages.album
  (:require [clojure.repl :as repl]
            [reagent.core :as r]
            [datum.album.api]
            [datum.gui.controllers.show-album-overview
             :as show-album-overview]
            [datum.gui.controllers.edit-album-tags
             :as edit-album-tags]
            [datum.gui.components.header.state :as header]
            [datum.gui.pages.album.components]
            [datum.gui.url :as url]
            [datum.gui.pages.util :as util]))

(defn create-store [update-store album-id]
  {:header
   (header/get-state :album)

   :show-album-overview
   (show-album-overview/Context.
    nil

    (reify show-album-overview/Transaction
      (show-album-overview/update-context [_ f]
        (update-store #(update % :show-album-overview f))))

    (reify show-album-overview/Api
      (show-album-overview/overview [_ id k]
        (datum.album.api/overview id k)))

    album-id)

   :edit-album-tags
   (edit-album-tags/ClosedContext.
    (fn [f]
      (update-store #(update % :edit-album-tags f))))
   })

(defn render [store elem]
  (r/render [datum.gui.pages.album.components/page store] elem))

(defn render-loop [elem {:keys [album-id]}]
  (util/render-loop {:create-store #(create-store % album-id)
                     :render #(render % elem)}))
