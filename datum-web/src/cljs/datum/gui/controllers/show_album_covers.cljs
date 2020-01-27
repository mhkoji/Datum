(ns datum.gui.controllers.show-album-covers
  (:require [cljs.core.async :refer [go <! timeout]]
            [datum.gui.components.loading :refer [spinner]]
            [datum.gui.components.album :as album-components]))

(defprotocol Api
  (covers [this k]))

;; States
(defrecord Fetching [])

(defrecord Appending [covers])

(defrecord Loaded [covers])


(defrecord StateContainer [state update])

(defrecord Context [state-container api])


(defn call-partitioned [xs on-processing on-finished]
  (go (loop [[sub-xs & rest] (partition 100 100 [] xs)]
        (if (empty? sub-xs)
          (on-finished)
          (do (on-processing sub-xs)
              (<! (timeout 100))
              (recur rest))))))

(defn update-state [context f]
  ((-> context :state-container :update) f))

(defn run [context]
  (update-state context #(->Fetching))
  (covers (:api context)
   (fn [covers]
     (update-state context #(->Appending []))
     (call-partitioned covers
      (fn [sub]
        (update-state context #(update % :covers concat sub)))
      (fn []
        (update-state context #(->Loaded (:covers %))))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti component (fn [context _]
                      (type (-> context :state-container :state))))

(defmethod component :default [context on-click-tag-button]
  nil)

(defmethod component Fetching [context on-click-tag-button]
  [:div
   [spinner]])

(defmethod component Appending [context on-click-tag-button]
  (let [{:keys [covers]} (-> context :state-container :state)]
    [album-components/covers-component covers on-click-tag-button]))

(defmethod component Loaded [context on-click-tag-button]
  (let [{:keys [covers]} (-> context :state-container :state)]
    (if (empty? covers)
      [:div "EMPTY!"]
      [album-components/covers-component covers on-click-tag-button])))
