(ns datum.gui.browser.pages.tag
  (:require [reagent.core :as r]
            [datum.tag.api]
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
       :selected-p false}
      ]}

    :execute
    (letfn [(update-state [f]
              (update-store #(update-in % [:show-tags :state] f)))]
      (fn [tag-id]
        (datum.tag.api/tags (fn [tags]
         (let [items (map (fn [tag]
                            {:id         (:tag-id tag)
                             :name       (:name tag)
                             :url        (url/tag-contents tag)
                             :selected-p (= (:tag-id tag) tag-id)})
                          tags)]
           (update-state #(update % :items concat items)))))))}

   :show-covers
   {:state
    {:covers    []
     :loading-p false}

    :execute
    (letfn [(update-state [f]
              (update-store #(update-in % [:show-covers :state] f)))]
      (fn [tag-id]
        (update-state #(assoc % :loading-p true))
        (datum.tag.api/albums tag-id (fn [covers]
         (update-state #(-> %
                            (assoc :loading-p false)
                            (assoc :covers    covers)))))))}
   })

(defn create-renderer [elem tag-id]
  (fn [store]
    (r/render [datum.gui.browser.pages.tag.components/page
               {:tag-id      tag-id
                :header      (header/get-state :tag)
                :show-tags   (-> store :show-tags)
                :show-covers (-> store :show-covers)
                }]
              elem)))

(defn render-loop [elem {:keys [tag-id]}]
  (util/render-loop {:create-store create-store
                     :render       (create-renderer elem tag-id)
                     }))
