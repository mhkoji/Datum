(ns datum.album.api
  (:require [cljs.core.async :refer [chan go <! pipe]]
            [ajax.core]
            [datum.api :as api]
            [datum.image]
            [datum.album]))

(defn obj->image [x]
  (datum.image/Image. (x "image-id")))


(defn obj->cover [x]
  (datum.album/Cover. (x "album-id")
                      (x "name")
                      (obj->image (x "thumbnail"))))

(defn covers [offset count k]
  (let [path "/album/covers"
        opts {:params {"offset" offset "count" count}}]
    (go (let [xs (<! (api/req ajax.core/GET path opts))]
          (k (map obj->cover xs))))))


(defn obj->overview [x]
  (datum.album/Overview. (x "album-id")
                         (x "name")
                         (map obj->image (x "pictures"))))

(defn overview [album-id k]
  (let [path (str "/album/" album-id "/overview")]
    (go (let [x (<! (api/req ajax.core/GET path))]
          (k (obj->overview x))))))
