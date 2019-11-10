(ns datum.gui.controllers.search-albums
  (:require [datum.gui.url :as url]))

(defrecord State [keyword])

(defrecord StateContainer [state update])

(defrecord Context [state-container redirect-to])

(defn change-keyword [context keyword]
  ((-> context :state-container :update) #(assoc % :keyword keyword)))

(defn start-searching [context]
  (when-let [{:keys [keyword]} (-> context :state-container :state)]
    ((-> context :redirect-to) (url/albums-search keyword))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn header-form [context]
  (letfn [(handle-change [evt]
            (change-keyword context (.-value (.-target evt))))
          (handle-submit [evt]
            (.preventDefault evt)
            (start-searching context))]
    (let [{:keys [keyword]} (-> context :state-container :state)]
      [:form {:class "form-inline my-2 my-lg-0"
              :on-submit handle-submit}
       [:input {:class "form-control mr-sm-2"
                :type "search"
                :placeholder "Search"
                :aria-label "search"
                :value keyword
                :on-change handle-change}]])))

