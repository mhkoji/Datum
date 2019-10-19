(ns datum.gui.controllers.show-album-covers
  (:require [cljs.core.async :refer [go <! timeout]]
            [datum.gui.components.loading :refer [spinner]]
            [datum.gui.components.album :as album-components]))

(defprotocol Api
  (covers [this k]))

(defprotocol Transaction
  (update-context [this f]))

(defrecord Context [state transaction api])

(defn update-state [context f]
  (update-context (-> context :transaction) #(update % :state f)))


;; States
(defrecord Fetching [])

(defrecord Appending [covers])

(defrecord Loaded [covers])


(defn call-partitioned [xs on-processing on-finished]
  (go (loop [[sub-xs & rest] (partition 100 100 [] xs)]
        (if (empty? sub-xs)
          (on-finished)
          (do (on-processing sub-xs)
              (<! (timeout 100))
              (recur rest))))))

(defn run [context]
  (update-state context #(Fetching.))
  (covers (-> context :api)
   (fn [covers]
     (update-state context #(Appending. []))
     (call-partitioned covers
      (fn [sub]
        (update-state context #(update % :covers concat sub)))
      (fn []
        (update-state context #(Loaded. (:covers %))))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti component (fn [state _] (type state)))

(defmethod component :default [state]
  nil)

(defmethod component Fetching [state on-click-tag-button]
  [:div
   [spinner]
   [album-components/placeholder-covers-component {:num 20}]])

(defmethod component Appending [state on-click-tag-button]
  (let [{:keys [covers]} state]
    [album-components/covers-component covers on-click-tag-button]))

(defmethod component Loaded [state on-click-tag-button]
  (let [{:keys [covers]} state]
    (if (empty? covers)
      [:div "EMPTY!"]
      [album-components/covers-component covers on-click-tag-button])))
