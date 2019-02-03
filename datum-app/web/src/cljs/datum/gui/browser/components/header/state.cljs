(ns datum.gui.browser.components.header.state
  (:require [datum.gui.browser.url :as url]))

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
