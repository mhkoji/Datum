(ns datum.gui.components.header.reagent)

(defn header-component [{:keys [brand pages]}]
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

    [:ul {:class "navbar-nav flex-row ml-md-auto d-none d-md-flex"}
     [:li {:class "nav-item dropdown"}
      [:a {:class "nav-link dropdown-toggle"
           :href "#"
           :id "datum-header-user-dropdown"
           :role "button"
           :data-toggle "dropdown"
           :aria-haspopup "true"
           :aria-expanded "false"}
       [:span {:class "oi oi-person" :aria-hidden "true"}]]
      [:ul {:class "dropdown-menu dropdown-menu-right"
            :aria-labelledby "datum-header-user-dropdown"}
       [:li {:class "dropdown-item"} [:a nil "Signed in as datum"]]
       [:li {:class "dropdown-divider"}]
       [:li {:class "dropdown-item"} [:a {:href "#"} "Sign out"]]]]]]])
