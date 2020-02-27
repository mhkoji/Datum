(ns datum.gui.components.loading)

(defn spinner []
  [:div {:class "d-flex justify-content-center"}
   [:div {:class "spinner-border m-5"
          :style {:width "3rem" :height "3rem"}
          :role "status"}
    [:span {:class "sr-only"} "Loading..."]]])
