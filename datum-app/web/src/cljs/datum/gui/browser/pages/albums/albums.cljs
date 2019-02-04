(ns datum.gui.browser.pages.albums
  (:require [clojure.browser.repl :as repl]
            [cljs.reader :refer [read-string]]
            [reagent.core :as r]
            [goog.Uri :as guri]
            [datum.album.show-covers]
            [datum.album.api]
            [datum.gui.browser.components.header.state :as header]
            [datum.gui.browser.pages.albums.components]
            [datum.gui.browser.url :as url]
            [datum.gui.browser.util :as util]))

(defn create-store [update-store]
  {:show-covers
   {:state
    (datum.album.show-covers/State. [] true)

    :execute
    (fn [offset count]
      (datum.album.show-covers/execute

       (reify datum.album.show-covers/Transaction
         (datum.album.show-covers/update-state [_ f]
           (update-store #(update-in % [:show-covers :state] f))))

       (reify datum.album.show-covers/Api
         (datum.album.show-covers/covers [this offset count k]
           (datum.album.api/covers offset count k)))

       offset

       count))
    }})


(defn create-renderer [elem offset count]
  (fn [store]
    (r/render [datum.gui.browser.pages.albums.components/page
               {:header
                (header/get-state :album)

                :pager
                {:prev (if (<= count offset)
                         {:link (url/albums (- offset count) count)
                          :enabled true}
                         {:link ""
                          :enabled false})
                 :next {:link (url/albums (+ offset count) count)
                        :enabled true}}

                :show-covers
                (let [{:keys [state execute]} (-> store :show-covers)]
                  {:state state
                   :execute #(execute offset count)})
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
    (util/render-loop {:create-store create-store
                       :render (create-renderer elem offset count)})))
