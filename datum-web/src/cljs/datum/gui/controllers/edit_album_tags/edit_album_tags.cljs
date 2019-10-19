(ns datum.gui.controllers.edit-album-tags
  (:require [datum.tag :as tag]
            [datum.gui.controllers.edit-album-tags.loading :as loading]
            [datum.gui.controllers.edit-album-tags.editing :as editing]
            [datum.gui.controllers.edit-album-tags.saving :as saving]
            [datum.gui.controllers.edit-album-tags.components
             :as components]))

(defprotocol Transaction
  (update-context [this f]))

(defn get-context [tran context-getter]
  (letfn [(consume-without-modification [context]
            (context-getter context)
            context)]
    (update-context tran consume-without-modification)))


(defrecord ClosedContext [transaction])

(defn saving-context [tran album-id attached-tags]
  (saving/Context.
   (saving/State. ::saving/saving)

   #(update-context tran %)

   album-id

   attached-tags

   (fn []
     (update-context tran #(ClosedContext. tran)))))

(defn editing-context [tran album-id tags attached-tags]
  (editing/Context.
   (editing/State. tags (tag/AttachedTagSet. attached-tags) "")

   #(update-context tran %)

   (fn [attached-tags]
     (update-context tran #(saving-context tran album-id attached-tags)))

   (fn []
     (update-context tran #(ClosedContext. tran)))))

(defn loading-context [tran album-id]
  (loading/Context.
   nil

   #(get-context tran %)

   #(update-context tran %)

   album-id

   (fn [tags attached-tags]
     (update-context tran #(editing-context
                            tran album-id tags attached-tags)))))


(defn start [context album-id]
  (when (= (type context) ClosedContext)
    (let [tran (:transaction context)]
      (update-context tran #(loading-context tran album-id)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti modal
  (fn [context] (type context)))

(defmethod modal ClosedContext [context]
  nil)

(defmethod modal loading/Context [context]
  [components/loading-modal context])

(defmethod modal editing/Context [context]
  [components/editing-modal context])

(defmethod modal saving/Context [context]
  [components/saving-modal context])
