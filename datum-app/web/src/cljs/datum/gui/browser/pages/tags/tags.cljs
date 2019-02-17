(ns datum.gui.browser.pages.tags
  (:require [reagent.core :as r]
            [datum.tag.api]
            [datum.album.api]
            [datum.gui.browser.components.header.state :as header]
            [datum.gui.browser.pages.tags.components]
            [datum.gui.browser.url :as url]
            [datum.gui.browser.util :as util]))

(defn create-store [update-store]
  {:show-tags
   {:state
    {:items
     [{:id         "__all"
       :name       "All"
       :url        (url/tags)
       :selected-p true}
      ]}

    :execute
    (letfn [(update-state [f]
              (update-store #(update-in % [:show-tags :state] f)))]
      (fn []
        (datum.tag.api/tags (fn [tags]
         (let [items (map (fn [tag]
                            {:id         (:tag-id tag)
                             :name       (:name tag)
                             :url        (url/tag-contents tag)
                             :selected-p false})
                          tags)]
           (update-state #(update % :items concat items)))))))}

   :show-covers
   {:state
    {:covers    []
     :loading-p false}

    :execute
    (letfn [(update-state [f]
              (update-store #(update-in % [:show-covers :state] f)))]
      (fn []
        (update-state #(assoc % :loading-p true))
        (datum.album.api/covers 0 100 (fn [covers]
         (update-state #(-> %
                            (assoc :loading-p false)
                            (assoc :covers    covers)))))))}
   })

(defn create-renderer [elem]
  (fn [store]
    (r/render [datum.gui.browser.pages.tags.components/page
               {:header      (header/get-state :tag)
                :show-tags   (-> store :show-tags)
                :show-covers (-> store :show-covers)
                }]
              elem)))

(defn render-loop [elem _]
  (util/render-loop {:create-store create-store
                     :render       (create-renderer elem)
                     }))
