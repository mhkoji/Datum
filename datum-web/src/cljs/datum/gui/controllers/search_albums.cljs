(ns datum.gui.controllers.search-albums
  (:require [datum.gui.url :as url]))

(defrecord State [keyword])

(defrecord Context [state update-state redirect-to])

(defn change-keyword [context keyword]
  ((:update-state context) #(assoc % :keyword keyword)))

(defn start-searching [context]
  (when-let [{:keys [keyword]} (:state context)]
    ((:redirect-to context) (url/albums-search keyword))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn header-form [context]
  (letfn [(handle-change [evt]
            (change-keyword context (.-value (.-target evt))))
          (handle-submit [evt]
            (.preventDefault evt)
            (start-searching context))]
    (let [{:keys [keyword]} (:state context)]
      [:form {:class "form-inline my-2 my-lg-0"
              :on-submit handle-submit}
       [:input {:class "form-control mr-sm-2"
                :type "search"
                :placeholder "Search"
                :aria-label "search"
                :value keyword
                :on-change handle-change}]])))

