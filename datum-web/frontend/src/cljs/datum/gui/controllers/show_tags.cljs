(ns datum.gui.controllers.show-tags
  (:require [datum.gui.url :as url]
            [datum.gui.components.tag :as components]))

(defprotocol Api
  (tags [this k]))

(defrecord StateContainer [state update])

(defrecord Context [state-container api selected-tag-id])

(defn update-state [context f]
  ((-> context :state-container :update) f))

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


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn component [context]
  [components/menu-component (-> context :state-container :state)])
