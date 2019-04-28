(ns datum.gui.pages.tags
  (:require [reagent.core :as r]
            [datum.tag.api]
            [datum.album.api]
            [datum.gui.controllers.show-tags :as show-tags]
            [datum.gui.controllers.show-album-covers :as show-album-covers]
            [datum.gui.components.header.state :as header]
            [datum.gui.components.header.reagent :refer [header-component]]
            [datum.gui.components.tag :as tag]
            [datum.gui.components.album :refer [covers-component]]
            [datum.gui.url :as url]
            [datum.gui.pages.util :as util]))

(defn page [{:keys [show-tags show-album-covers]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      (show-tags/run (-> show-tags :context))
      (show-album-covers/run (-> show-album-covers :context)))

    :reagent-render
    (fn [{:keys [header show-tags show-album-covers]}]
      [:div
       ;; header
       [header-component header]

       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Tags"]

        [tag/menu-component (-> show-tags :state)]

        ;; covers
        (let [{:keys [type covers]} (-> show-album-covers :state)]
          [:main {:class "pt-3 px-4"}

           (if (= type :loading)
             [:div "Loading..."]
             [covers-component covers])])
        ]])
    }))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-store [update-store]
  {:header
   (header/get-state :tag)

   :show-tags
   {:state nil

    :context
    (show-tags/Context.
     (reify show-tags/Transaction
       (show-tags/update-state [_ f]
         (update-store #(update-in % [:show-tags :state] f))))

     (reify show-tags/Api
       (show-tags/tags [_ k]
         (datum.tag.api/tags k)))

     nil)
    }

   :show-album-covers
   {:state nil

    :context
    (show-album-covers/Context.
     (reify show-album-covers/Transaction
       (show-album-covers/update-state [_ f]
         (update-store #(update-in % [:show-album-covers :state] f))))

     (reify show-album-covers/Api
       (show-album-covers/covers [_ k]
         (datum.album.api/covers 0 100 k))))
    }})

(defn create-renderer [elem]
  (fn [store] (r/render [page store] elem)))

(defn render-loop [elem _]
  (util/render-loop {:create-store create-store
                     :render       (create-renderer elem)
                     }))
