(ns datum.gui.components.tag)

(defn button [{:keys [on-click]}]
  [:button {:type "button"
            :class "btn btn-outline-secondary"
            :on-click on-click}
   "Tags"])
