(ns datum.gui.pages.albums
  (:require [cljs.reader :refer [read-string]]
            [reagent.core :as r]
            [goog.Uri :as guri]
            [datum.album.api]
            [datum.gui.controllers.show-album-covers :as show-album-covers]
            [datum.gui.controllers.edit-album-tags :as edit-album-tags]
            [datum.gui.components.header.state :as header]
            [datum.gui.pages.albums.components]
            [datum.gui.url :as url]
            [datum.gui.pages.util :as util]))

(defn create-truth-covers [update-truth offset count]
  {:header
   (header/get-state :album)

   :pager
   {:prev (if (<= count offset)
            {:link (url/albums (- offset count) count) :enabled true}
            {:link ""                                  :enabled false})
    :next {:link (url/albums (+ offset count) count) :enabled true}}

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
   (edit-album-tags/ClosedContext.
    (reify edit-album-tags/Transaction
      (edit-album-tags/update-context [_ f]
        (update-truth #(update % :edit-album-tags f)))))

   :search-albums
   {:keyword ""
    :on-change-keyword
    (fn [keyword]
      (update-truth #(assoc-in % [:search-albums :keyword] keyword)))
    :on-search
    (fn [self]
      (when-let [keyword (-> self :keyword)]
        (set! (.-location js/window) (url/albums-search keyword))))}
   })

(defn create-truth-search [update-truth keyword]
  {:header
   (header/get-state :album)

   :show-album-covers
   (show-album-covers/Context.
    nil

    (reify show-album-covers/Transaction
      (show-album-covers/update-context [_ f]
        (update-truth #(update % :show-album-covers f))))

    (reify show-album-covers/Api
      (show-album-covers/covers [_ k]
        (datum.album.api/search keyword k))))

   :edit-album-tags
   (edit-album-tags/ClosedContext.
    (reify edit-album-tags/Transaction
      (edit-album-tags/update-context [_ f]
        (update-truth #(update % :edit-album-tags f)))))

   :search-albums
   {:keyword keyword
    :on-change-keyword
    (fn [keyword]
      (update-truth #(assoc-in % [:search-albums :keyword] keyword)))
    :on-search
    (fn [self]
      (when-let [keyword (-> self :keyword)]
        (set! (.-location js/window) (url/albums-search keyword))))}
   })

(defn render [truth elem]
  (r/render [datum.gui.pages.albums.components/page truth] elem))

(defn render-loop [elem _]
  (util/render-loop
   {:create-store
    (let [search (.-search js/location)
          query-data (guri/QueryData.
                      (if (= search "") "" (subs search 1)))]
      (if-let [keyword (.get query-data "keyword")]
        #(create-truth-search % keyword)
        (let [offset (read-string (.get query-data "offset" "0"))
              count (read-string (.get query-data "count" "500"))]
          #(create-truth-covers % offset count))))
    :render #(render % elem)}))
