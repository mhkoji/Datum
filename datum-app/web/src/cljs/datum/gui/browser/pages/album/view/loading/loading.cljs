(ns datum.gui.browser.pages.album.view.loading
  (:require [reagent.core :as r]
            [datum.gui.browser.components.header.state :as header]
            [datum.gui.browser.components.header.reagent
             :refer [header-component]]))

(defprotocol Transaction
  (update-state [this f]))

(defprotocol Api
  (images [this k]))

(defrecord State [images])


(defn load-images [transaction api]
  (images api album-id (fn [images]
    (update-state transaction #(assoc % :images images)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn page [{:keys [on-show]}]
  (r/create-class
   {:component-did-mount
    (fn [this]
      (on-show))
    :reagent-render
    (fn []
      [:div
       [header-component (header/get-state :album)]
       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Album"]
        [:div "Loading..."]]])}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-store [update-store]
  {:loading
   {:state (State. nil)
    :load-images
    (fn [album-id]
      (load-images
       (reify Transaction
         (update-state []
           (update-store #(update-in % [:loading :state] f))))

       (reify Api
         (images [_ k]
           (datum.album.api/overview album-id #(k (-> % :pictures)))))))
    }})

(defn store-state [store]
  (-> store :state))

(defn create-renderer [elem album-id]
  (fn [store]
    (r/render [page
               {:on-show
                #((-> store :loading :load-images) album-id)}]
              elem)))
