(ns datum.album.api
  (:require [cljs.core.async :refer [go <! pipe]]
            [ajax.core]
            [datum.api :as api]
            [datum.image]
            [datum.album]
            [datum.tag]))

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


(defn obj->tag [x]
  (datum.tag/Tag. (str (x "tag-id")) (x "name")))

(defn tags [album-id k]
  (let [path (str "/album/" album-id "/tags")]
    (go (let [xs (<! (api/req ajax.core/GET path))]
          (k (map obj->tag xs))))))

(defn put-tags [album-id tags k]
  (let [path (str "/album/" album-id "/tags")
        opts {:params {:tag_ids (map :tag-id tags)}}]
    (go (<! (api/req ajax.core/PUT path opts))
        (k))))
