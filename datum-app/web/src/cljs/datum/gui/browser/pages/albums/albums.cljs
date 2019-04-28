(ns datum.gui.browser.pages.albums
  (:require [cljs.reader :refer [read-string]]
            [reagent.core :as r]
            [goog.Uri :as guri]
            [datum.album.api]
            [datum.gui.browser.controllers.show-album-covers
             :as show-album-covers]
            [datum.gui.browser.controllers.edit-album-tags
             :as edit-album-tags]
            [datum.gui.browser.components.header.state :as header]
            [datum.gui.browser.pages.albums.components]
            [datum.gui.browser.url :as url]
            [datum.gui.browser.util :as util]))

(defn create-store [update-store offset count]
  {:pager
   {:offset offset :count count}

   :show-album-covers
   {:state nil

    :transaction
    (reify show-album-covers/Transaction
      (show-album-covers/update-state [_ f]
        (update-store #(update-in % [:show-album-covers :state] f))))

    :api
    (reify show-album-covers/Api
      (show-album-covers/covers [_ k]
        (datum.album.api/covers offset count k)))
    }

   :edit-album-tags
   (edit-album-tags/closed-store
    (fn [f]
      (update-store #(update % :edit-album-tags f))))
   })


(defn create-renderer [elem]
  (fn [store]
    (r/render [datum.gui.browser.pages.albums.components/page
               {:header
                (header/get-state :album)

                :pager
                (let [{:keys [offset count]} (-> store :pager)]
                  {:prev (if (<= count offset)
                           {:link (url/albums (- offset count) count)
                            :enabled true}
                           {:link ""
                            :enabled false})
                   :next {:link (url/albums (+ offset count) count)
                          :enabled true}})

                :show-album-covers
                (-> store :show-album-covers)

                :edit-album-tags
                (-> store :edit-album-tags)
                }]
              elem)))


(defn render-loop [elem _]
  (let [search
        (.-search js/location)
        query-data
        (guri/QueryData. (if (= search "") "" (subs search 1)))
        offset
        (read-string (.get query-data "offset" "0"))
        count
        (read-string (.get query-data "count" "500"))]
    (util/render-loop {:create-store #(create-store % offset count)
                       :render (create-renderer elem)})))
