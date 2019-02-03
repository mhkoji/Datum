(ns datum.album.api
  (:require [cljs.core.async :refer [chan go <! pipe]]
            [ajax.core]
            [datum.api :as api]
            [datum.image]
            [datum.album]))

(defn obj->cover [x]
  (datum.album/Cover.
   (x "album-id")
   (x "name")
   (datum.image/Image.
    ((x "thumbnail") "image-id"))))

(defn covers [offset count k]
  (let [opts {:params {"offset" offset "count" count}}]
    (go (let [xs (<! (api/req ajax.core/GET "/album/covers" opts))]
          (k (map obj->cover xs))))))
