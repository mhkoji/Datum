(ns datum.gui.browser.pages.album.view.single
  (:require [reagent.core :as r]
            [datum.viewer]
            [datum.gui.browser.components.header.reagent
             :refer [header-component]]
            [datum.gui.browser.components.header.state :as header]
            [datum.gui.browser.util :as util]
            [datum.gui.browser.pages.album.view.single.components :as c]))

(defn create-store [update-store images]
  {:viewer
   (let [transaction
         (reify datum.viewer/Transaction
           (datum.viewer/update-state [_ f]
             (update-store #(update-in % [:viewer :state] f))))]
     {:state
      (datum.viewer/State. images 0 nil)

      :increment-index
      #(datum.viewer/increment-index transaction %)

      :set-size
      #(datum.viewer/set-size transient %)})})

(defn create-renderer [elem album-id]
  (fn [store]
    (let [viewer (-> store :viewer)]
      (r/render [c/page
                 {:album-id        album-id
                  :state           (-> viewer :state)
                  :increment-index (-> viewer :increment-index)
                  :set-size        (-> viewer :set-size)}]
                elem))))
