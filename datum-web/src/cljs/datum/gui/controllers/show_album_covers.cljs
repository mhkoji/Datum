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


(defrecord Context [state update-state api])


(defn call-partitioned [xs on-processing on-finished]
  (go (loop [[sub-xs & rest] (partition 100 100 [] xs)]
        (if (empty? sub-xs)
          (on-finished)
          (do (on-processing sub-xs)
              (<! (timeout 100))
              (recur rest))))))

(defn run [context]
  ((:update-state context) #(->Fetching))
  (covers (:api context)
   (fn [covers]
     ((:update-state context) #(->Appending []))
     (call-partitioned covers
      (fn [sub]
        ((:update-state context) #(update % :covers concat sub)))
      (fn []
        ((:update-state context) #(->Loaded (:covers %))))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti component (fn [context _] (type (:state context))))

(defmethod component :default [context on-click-tag-button]
  nil)

(defmethod component Fetching [context on-click-tag-button]
  [:div
   [spinner]
   [album-components/placeholder-covers-component {:num 20}]])

(defmethod component Appending [context on-click-tag-button]
  (let [{:keys [covers]} (:state context)]
    [album-components/covers-component covers on-click-tag-button]))

(defmethod component Loaded [context on-click-tag-button]
  (let [{:keys [covers]} (:state context)]
    (if (empty? covers)
      [:div "EMPTY!"]
      [album-components/covers-component covers on-click-tag-button])))
