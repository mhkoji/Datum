(ns datum.frequently-accessed
  (:require [cljs.core.async :refer [go <! pipe]]
            [ajax.core]
            [datum.api :as api]
            [datum.album]))

(defn obj->image [x]
  (datum.image/->Image (x "image-id")))

(defn obj->cover [x]
  (datum.album/->Cover (x "album-id")
                       (x "name")
                       (obj->image (x "thumbnail"))))

(defn album-covers [k]
  (let [path (str "/frequently-accessed/album/covers")]
    (go (let [xs (<! (api/req ajax.core/GET path))]
          (k (map obj->cover xs))))))
