(ns datum.gui.browser.url)

(defn album [album-id]
  (str "/album/" album-id))

(defn album-viewer-single [album-id image]
  (str "/album/" album-id
       "/view?viewer=single&current=" (-> image :image-id)))

(defn albums
  ([]
   "/albums")
  ([offset count]
   (str "/albums?offset=" (max 0 offset) "&count=" count)))


(defn tags []
  "/tags")


(defn image [image]
  (str "/api/image/" (-> image :image-id)))
