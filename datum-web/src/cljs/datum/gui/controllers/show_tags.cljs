(ns datum.gui.controllers.show-tags
  (:require [datum.gui.url :as url]))

(defprotocol Api
  (tags [this k]))

(defprotocol Transaction
  (update-context [this f]))

(defrecord Context [state transaction api selected-tag-id])

(defn update-state [context f]
  (update-context (-> context :transaction) #(update % :state f)))


(defn run [context]
  (let [{:keys [api selected-tag-id]} context]
    (update-state context
     (fn [_] {:items [{:id         "__all"
                       :name       "All"
                       :url        (url/tags)
                       :selected-p (not selected-tag-id)}]}))
    (tags api
     (fn [tags]
       (let [items (map (fn [tag]
                          {:id         (:tag-id tag)
                           :name       (:name tag)
                           :url        (url/tag-contents tag)
                           :selected-p (= (:tag-id tag) selected-tag-id)})
                        tags)]
         (update-state context #(update % :items concat items)))))))
