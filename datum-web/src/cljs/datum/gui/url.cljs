(ns datum.gui.url)

(defn album [album-id]
  (str "/album/" album-id))

(defn album-viewer-single [album-id image]
  (str "/album/" album-id
       "/view?viewer=single&current=" (-> image :image-id)))

(defn albums-search [keyword]
  (str "/albums?keyword=" keyword))

(defn albums
  ([]
   "/albums")
  ([offset count]
   (str "/albums?offset=" (max 0 offset) "&count=" count)))


(defn tags []
  "/tags")

(defn tag-contents [tag]
  (str "/tag/" (:tag-id tag)))


(defn image [image]
  (str "/api/image/" (-> image :image-id)))
