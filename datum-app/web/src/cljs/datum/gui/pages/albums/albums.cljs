(ns datum.gui.pages.albums
  (:require [cljs.reader :refer [read-string]]
            [reagent.core :as r]
            [goog.Uri :as guri]
            [datum.album.api]
            [datum.gui.controllers.show-album-covers
             :as show-album-covers]
            [datum.gui.controllers.edit-album-tags
             :as edit-album-tags]
            [datum.gui.components.header.state :as header]
            [datum.gui.pages.albums.components]
            [datum.gui.url :as url]
            [datum.gui.pages.util :as util]))

(defn create-truth [update-truth offset count]
  {:pager
   {:offset offset :count count}

   :show-album-covers
   (show-album-covers/Context.
    nil

    (reify show-album-covers/Transaction
      (show-album-covers/update-context [_ f]
        (update-truth #(update % :show-album-covers f))))

    (reify show-album-covers/Api
      (show-album-covers/covers [_ k]
        (datum.album.api/covers offset count k))))

   :edit-album-tags
   (edit-album-tags/closed-store
    (fn [f]
      (update-truth #(update % :edit-album-tags f))))
   })


(defn create-renderer [elem]
  (fn [truth]
    (r/render [datum.gui.pages.albums.components/page
               {:header
                (header/get-state :album)

                :pager
                (let [{:keys [offset count]} (-> truth :pager)]
                  {:prev (if (<= count offset)
                           {:link (url/albums (- offset count) count)
                            :enabled true}
                           {:link ""
                            :enabled false})
                   :next {:link (url/albums (+ offset count) count)
                          :enabled true}})

                :show-album-covers
                (-> truth :show-album-covers)

                :edit-album-tags
                (-> truth :edit-album-tags)
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
    (util/render-loop {:create-store #(create-truth % offset count)
                       :render (create-renderer elem)})))
