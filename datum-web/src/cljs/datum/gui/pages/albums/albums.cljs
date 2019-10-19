(ns datum.gui.pages.albums
  (:require [cljs.reader :refer [read-string]]
            [reagent.core :as r]
            [goog.Uri :as guri]
            [datum.album.api]
            [datum.gui.controllers.search-albums :as search-albums]
            [datum.gui.controllers.show-album-covers :as show-album-covers]
            [datum.gui.controllers.edit-album-tags :as edit-album-tags]
            [datum.gui.components.header.state :as header]
            [datum.gui.pages.albums.components]
            [datum.gui.url :as url]
            [datum.gui.pages.util :as util]))


(defn create-truth [update! keyword]
  {:header
   (header/get-state :album)

   :edit-album-tags
   (edit-album-tags/ClosedContext.
    (reify edit-album-tags/Transaction
      (edit-album-tags/update-context [_ f]
        (update! #(update % :edit-album-tags f)))))

   :search-albums
   (search-albums/Context.
    (search-albums/State. keyword)

    (fn [f]
      (update! #(update % :search-albums f)))

    (fn [url]
      (set! (.-location js/window) url)))
   })

(defn create-truth-covers [update! offset count]
  {:pager
   {:prev (if (<= count offset)
            {:link (url/albums (- offset count) count) :enabled true}
            {:link ""                                  :enabled false})
    :next {:link (url/albums (+ offset count) count) :enabled true}}

   :show-album-covers
   (show-album-covers/Context.
    nil

    (reify show-album-covers/Transaction
      (show-album-covers/update-context [_ f]
        (update! #(update % :show-album-covers f))))

    (reify show-album-covers/Api
      (show-album-covers/covers [_ k]
        (datum.album.api/covers offset count k))))
   })

(defn create-truth-search [update!]
  {:show-album-covers
   (show-album-covers/Context.
    nil

    (reify show-album-covers/Transaction
      (show-album-covers/update-context [_ f]
        (update! #(update % :show-album-covers f))))

    (reify show-album-covers/Api
      (show-album-covers/covers [_ k]
        (datum.album.api/search keyword k))))
   })

(defn render [truth elem]
  (r/render [datum.gui.pages.albums.components/page truth] elem))

(defn render-loop [elem _]
  (util/render-loop
   {:create-store
    (let [search (.-search js/location)
          query-data (guri/QueryData.
                      (if (= search "") "" (subs search 1)))
          keyword (.get query-data "keyword")]
      (if keyword
        #(merge (create-truth % keyword)
                (create-truth-search %))
        (let [offset (read-string (.get query-data "offset" "0"))
              count (read-string (.get query-data "count" "500"))]
          #(merge (create-truth % nil)
                  (create-truth-covers % offset count)))))
    :render #(render % elem)}))
