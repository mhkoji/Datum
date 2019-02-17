(ns datum.gui.browser.main
  (:require [bidi.bidi :refer [match-route]]
            [goog.dom :as gdom]
            [datum.gui.browser.pages.album]
            [datum.gui.browser.pages.album.view]
            [datum.gui.browser.pages.albums]
            [datum.gui.browser.pages.tags]))

(def *routes*
  ["/"
   {["album/" :album-id]
    datum.gui.browser.pages.album/render-loop

    ["album/" :album-id "/view"]
    datum.gui.browser.pages.album.view/render-loop

    "albums"
    datum.gui.browser.pages.albums/render-loop

    "tags"
    datum.gui.browser.pages.tags/render-loop
    }])

(let [{:keys [handler route-params]}
      (match-route *routes* (.-pathname js/location))]
  (handler (gdom/getElement "app") route-params))
