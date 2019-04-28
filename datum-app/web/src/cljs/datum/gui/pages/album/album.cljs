(ns datum.gui.pages.album
  (:require [clojure.repl :as repl]
            [reagent.core :as r]
            [datum.album.show-overview]
            [datum.album.api]
            [datum.gui.controllers.edit-album-tags
             :as edit-album-tags]
            [datum.gui.components.header.state :as header]
            [datum.gui.pages.album.components]
            [datum.gui.url :as url]
            [datum.gui.pages.util :as util]))

(defn create-store [update-store]
  {:show-overview
   {:state
    (datum.album.show-overview/State. nil)

    :execute
    (fn [state album-id]
      (datum.album.show-overview/execute

       (reify datum.album.show-overview/Transaction
         (datum.album.show-overview/get-state [_]
           state)

         (datum.album.show-overview/update-state [_ f]
           (update-store #(update-in % [:show-overview :state] f))))

       (reify datum.album.show-overview/Api
         (datum.album.show-overview/overview [_ id k]
           (datum.album.api/overview id k)))

       album-id))}

   :edit-album-tags
   (edit-album-tags/closed-store
    (fn [f] (update-store #(update % :edit-album-tags f))))
   })

(defn create-renderer [elem album-id]
  (fn [store]
    (r/render [datum.gui.pages.album.components/page
               {:header
                (header/get-state :album)

                :show-overview
                (let [{:keys [state execute]} (-> store :show-overview)]
                  {:state state
                   :execute #(execute state album-id)})

                :edit-album-tags
                (-> store :edit-album-tags)
                }]
              elem)))


(defn render-loop [elem {:keys [album-id]}]
  (util/render-loop {:create-store create-store
                     :render (create-renderer elem album-id)}))
