(ns datum.gui.pages.frequently-accessed
  (:require [reagent.core :as r]
            [datum.frequently-accessed]
            [datum.api.tag]
            [datum.gui.components.header.state :as header]
            [datum.gui.components.header.reagent :refer [header-component]]
            [datum.gui.components.album :refer [covers-component]]
            [datum.gui.pages.util :as util]))

(defrecord Album [status covers])

(defrecord State [album])

(defrecord Context [state update-context])

(defn create-truth [update-truth]
  {:header
   (header/get-state nil)

   :frequently-accessed
   (Context.
    nil

    (fn [f] (update-truth #(update % :frequently-accessed f))))
   })

(defn update-state [context f]
  (let [update-context (:update-context context)]
    (update-context #(update % :state f))))

(defn run [context]
  (update-state context #(assoc % :album (Album. :loading [])))
  (datum.frequently-accessed/album-covers
   (fn [covers]
     (update-state context
                   #(-> %
                        (assoc-in [:album :status] :loaded)
                        (assoc-in [:album :covers] covers))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn page [{:keys [frequently-accessed]}]
  (r/create-class
   {:component-did-mount
    (fn [comp]
      (run frequently-accessed))

    :reagent-render
    (fn [{:keys [header frequently-accessed]}]
      [:div
       ;; header
       [header-component header]

       [:main {:class "pt-3 px-4"}
        [:h1 {:class "h2"} "Frequently accessed"]

        ;; covers
        (let [{:keys [status covers]}
              (-> frequently-accessed :state :album)]
          [:main {:class "pt-3 px-4"}

           (if (= status :loading)
             [:div "Loading..."]
             [:div {:class "container"}
              [covers-component covers]])])
        ]])
    }))

(defn render [store elem]
  (r/render [page store] elem))

(defn render-loop [elem _]
  (util/render-loop {:create-store #(create-truth %)
                     :render       #(render % elem)}))
