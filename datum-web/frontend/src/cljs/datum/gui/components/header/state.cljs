(ns datum.gui.components.header.state
  (:require [datum.gui.url :as url]))

(defn get-state [in-page]
  {:brand {:name "Datum" :url (url/albums)}
   :pages [{:id "albums"
            :name "Albums"
            :url (url/albums)
            :active-p (= in-page :album)}
           {:id "tag"
            :name "Tags"
            :url (url/tags)
            :active-p (= in-page :tag)}]})
