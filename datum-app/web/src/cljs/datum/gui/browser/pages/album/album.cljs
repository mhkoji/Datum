(ns datum.gui.browser.pages.album
  (:require [clojure.browser.repl :as repl]
            [reagent.core :as r]
            [datum.album.show-overview]
            [datum.album.api]
            [datum.gui.browser.components.header.state :as header]
            [datum.gui.browser.pages.album.components]
            [datum.gui.browser.url :as url]
            [datum.gui.browser.util :as util]))

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
   })

(defn create-renderer [elem album-id]
  (fn [store]
    (r/render [datum.gui.browser.pages.album.components/page
               {:header
                (header/get-state :album)

                :show-overview
                (let [{:keys [state execute]} (-> store :show-overview)]
                  {:state state
                   :execute #(execute state album-id)})
                }]
              elem)))


(defn render-loop [elem {:keys [album-id]}]
  (util/render-loop {:create-store create-store
                     :render (create-renderer elem album-id)}))
