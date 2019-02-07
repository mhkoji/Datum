(ns datum.gui.browser.pages.album.view.loading
  (:require [reagent.core :as r]
            [datum.album.api]
            [datum.gui.browser.components.header.state :as header]
            [datum.gui.browser.components.header.reagent
             :refer [header-component]]))

(defprotocol Transaction
  (update-state [this f]))

(defprotocol Api
  (images [this k]))

(defrecord State [images])


(defn load-images [transaction api]
  (images api (fn [images]
    (update-state transaction #(assoc % :images images)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn page [{:keys [on-show]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      ((-> (r/props comp) :on-show)))
    :reagent-render
    (fn []
      [:div
       [header-component (header/get-state :album)]
       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Album"]
        [:div "Loading..."]]])}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-store [update-store album-id on-loaded]
  {:loading
   {:state (State. nil)
    :load-images
    (fn []
      (load-images

       (reify Transaction
         (update-state [_ f]
           (update-store #(update-in % [:loading :state] f))))

       (reify Api
         (images [_ k]
           (datum.album.api/overview album-id (fn [overview]
             (let [images (-> overview :pictures)]
               (on-loaded images)
               (k images))))))))
    }})

(defn create-renderer [elem]
  (fn [store]
    (r/render [page {:on-show (-> store :loading :load-images)}]
              elem)))
