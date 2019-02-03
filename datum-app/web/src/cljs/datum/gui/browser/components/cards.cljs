(ns datum.gui.components.cards)

(defn card-decks [num% items item-key item-render]
  (let [count (count items)]
    (let [num (min num% count)
          group (group-by first
                 (map-indexed (fn [index item]
                                (list (quot index num) item))
                              items))]
      [:div {:class "container"}
       (for [row-index (sort < (keys group))]
         ^{:key (str row-index)}
         [:div {:class "card-deck"}
          (for [[_ item] (group row-index)]
            ^{:key (item-key item)}
            [item-render item])])])))
