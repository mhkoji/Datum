(ns datum.api.tag
  (:require [cljs.core.async :refer [go <!]]
            [ajax.core]
            [datum.api :as api]
            [datum.image]
            [datum.album]
            [datum.tag]))

(defn obj->tag [x]
  (datum.tag/->Tag (str (x "tag-id")) (x "name")))

(defn tags [k]
  (go (let [xs (<! (api/req ajax.core/GET "/tags"))]
        (k (map obj->tag xs)))))

(defn put-tags [name k]
  (let [opts {:params {:name name}}]
    (go (<! (api/req ajax.core/PUT "/tags" opts))
        (k))))

(defn delete-tag [tag k]
  (let [path (str "/tag/" (:tag-id tag))]
    (go (<! (api/req ajax.core/DELETE path))
        (k))))


(defn obj->image [x]
  (datum.image/->Image (x "image-id")))

(defn obj->cover [x]
  (datum.album/->Cover (x "album-id")
                       (x "name")
                       (obj->image (x "thumbnail"))))

(defn albums [tag-id k]
  (let [path (str "/tag/" tag-id "/albums")]
    (go (let [xs (<! (api/req ajax.core/GET path))]
          (k (map obj->cover xs))))))
