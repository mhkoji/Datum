(ns datum.gui.pages.main
  (:require [bidi.bidi :refer [match-route]]
            [goog.dom :as gdom]
            [datum.gui.pages.frequently-accessed]
            [datum.gui.pages.album]
            [datum.gui.pages.album.view]
            [datum.gui.pages.albums]
            [datum.gui.pages.tags]
            [datum.gui.pages.tag]))

(def *routes*
  ["/"
   {["frequently-accessed"]
    datum.gui.pages.frequently-accessed/render-loop

    ["album/" :album-id]
    datum.gui.pages.album/render-loop

    ["album/" :album-id "/view"]
    datum.gui.pages.album.view/render-loop

    "albums"
    datum.gui.pages.albums/render-loop

    ["tag/" :tag-id]
    datum.gui.pages.tag/render-loop

    "tags"
    datum.gui.pages.tags/render-loop
    }])

(let [{:keys [handler route-params]}
      (match-route *routes* (.-pathname js/location))]
  (handler (gdom/getElement "app") route-params))
