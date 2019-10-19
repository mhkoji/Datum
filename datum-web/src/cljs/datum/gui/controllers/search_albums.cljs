(ns datum.gui.controllers.search-albums
  (:require [datum.gui.url :as url]))

(defrecord Context [state update-context redirect-to])

(defn get-state [context]
  (:state context))

(defn update-state [context f]
  ((:update-context context) #(update % :state f)))

(defn redirect-to [context url]
  ((:redirect-to context) url))


(defrecord State [keyword])

(defn change-keyword [context keyword]
  (update-state context #(assoc % :keyword keyword)))

(defn start-searching [context]
  (when-let [{:keys [keyword]} (get-state context)]
    (redirect-to context (url/albums-search keyword))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn header-form [context]
  (letfn [(handle-change [evt]
            (change-keyword context (.-value (.-target evt))))
          (handle-submit [evt]
            (.preventDefault evt)
            (start-searching context))]
    (let [{:keys [keyword]} (get-state context)]
      [:form {:class "form-inline my-2 my-lg-0"
              :on-submit handle-submit}
       [:input {:class "form-control mr-sm-2"
                :type "search"
                :placeholder "Search"
                :aria-label "search"
                :value keyword
                :on-change handle-change}]])))

