(ns datum.gui.browser.components.tag
  (:require [datum.gui.browser.url :as url]))

(defn menu-component [{:keys [items]}]
  [:ul {:class "nav nav-tabs"}
   (for [{:keys [id url name selected-p]} items]
     ^{:key id}
     [:li {:class "nav-item"}
      [:a {:class (str "nav-link" (if selected-p " active" ""))
           :href url}
       name]])])
