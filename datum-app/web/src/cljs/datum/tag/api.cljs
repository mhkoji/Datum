(ns datum.tag.api
  (:require [cljs.core.async :refer [go <!]]
            [ajax.core]
            [datum.api :as api]
            [datum.tag]))

(defn obj->tag [x]
  (datum.tag/Tag. (x "tag-id") (x "name")))

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
