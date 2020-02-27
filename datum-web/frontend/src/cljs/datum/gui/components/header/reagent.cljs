(ns datum.gui.components.header.reagent)

(defn header-component [{:keys [brand pages]} {:keys [form]}]
  [:nav {:class "navbar navbar-expand-lg navbar-dark bg-dark"}

   (let [{:keys [name url]} brand]
     [:a {:class "navbar-brand" :href url} name])

   [:button {:class "navbar-toggler"
             :type "button"
             :data-toggle "collapse"
             :data-target "#header-reagent-header-component-alt-markup"
             :aria-controls "navbarNavDropdown"
             :aria-expanded "false"
             :aria-label "Toggle navigation"}
    [:span {:class "navbar-toggler-icon"}]]

   [:div {:class "collapse navbar-collapse"
          :id "header-reagent-header-component-alt-markup"}
    [:ul {:class "navbar-nav mr-auto mt-2 mt-lg-0"}
     (for [page pages]
       (let [{:keys [id name url active-p]} page]
         ^{:key id}
         [:li {:class (str "nav-item"
                           (if active-p " active" ""))}
          [:a {:class "nav-link" :href url} name]]))]

    form
    ]])
