(ns datum.gui.pages.album.view.loading
  (:require [reagent.core :as r]
            [datum.album.api]
            [datum.gui.components.header.state :as header]
            [datum.gui.components.header.reagent :refer [header-component]]))

(defrecord State [status images])

(defrecord Context [state update-context album-id on-loaded])

(defn update-state [context f]
  (let [update-context (:update-context context)]
    (update-context #(update % :state f))))

(defn images [album-id k]
  (datum.album.api/overview album-id #(k (-> % :pictures))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn page [context]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      (images (:album-id context)
       (fn [images]
         (update-state context #(-> %
                                    (assoc :status :loaded)
                                    (assoc :images images))))))

    :component-did-update
    (fn [comp]
      (let [context (r/props comp)]
        (let [state (:state context)]
          (when (= (:status state) :loaded)
            ((:on-loaded context) (:images state))))))

    :reagent-render
    (fn []
      [:div
       [header-component (header/get-state :album)]
       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Album"]
        [:div "Loading..."]]])}))
