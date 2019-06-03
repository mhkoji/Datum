(ns datum.gui.pages.tag
  (:require [reagent.core :as r]
            [datum.tag.api]
            [datum.gui.controllers.show-tags :as show-tags]
            [datum.gui.controllers.show-album-covers :as show-album-covers]
            [datum.gui.components.header.state :as header]
            [datum.gui.components.header.reagent :refer [header-component]]
            [datum.gui.components.tag :as tag]
            [datum.gui.components.album :refer [covers-component]]
            [datum.gui.url :as url]
            [datum.gui.pages.util :as util]))

(defn ops-component [{:keys [on-edit on-delete]}]
  [:div {:class "btn-toolbar mb-2 mb-md-0"}
   [:div {:class "btn-group mr-2"}
    [:button {:class "btn btn-default btn-sm"
              :on-click on-edit}
     [:span {:class "oi oi-pencil" :aria-hidden "true"}]]]
   [:div {:class "btn-group mr-2"}
    [:button {:class "btn btn-danger btn-sm"
              :on-click on-delete}
     [:span {:class "oi oi-trash" :aria-hidden "true"}]]]])


(defn page [{:keys [show-tags show-album-covers]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      (show-tags/run show-tags)
      (show-album-covers/run show-album-covers))

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
             [:div {:class "container"}
              [covers-component covers]])])
        ]])
    }))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-store [update-store tag-id]
  {:header
   (header/get-state :tag)

   :show-tags
   (show-tags/Context.
    nil

    (reify show-tags/Transaction
      (show-tags/update-context [_ f]
        (update-store #(update % :show-tags f))))

    (reify show-tags/Api
      (show-tags/tags [_ k]
        (datum.tag.api/tags k)))

    tag-id)

   :show-album-covers
   (show-album-covers/Context.
    nil

    (reify show-album-covers/Transaction
      (show-album-covers/update-context [_ f]
        (update-store #(update % :show-album-covers f))))

    (reify show-album-covers/Api
      (show-album-covers/covers [_ k]
        (datum.tag.api/albums tag-id k))))
   })


(defn render [store elem]
  (r/render [page store] elem))

(defn render-loop [elem {:keys [tag-id]}]
  (util/render-loop {:create-store #(create-store % tag-id)
                     :render       #(render % elem)
                     }))
