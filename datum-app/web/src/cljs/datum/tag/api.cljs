(ns datum.tag.api
  (:require [cljs.core.async :refer [go <! pipe]]
            [ajax.core]
            [datum.api :as api]
            [datum.tag]))

(defn obj->tag [x]
  (datum.tag/Tag. (x "tag-id") (x "name")))

(defn tags [k]
  (go (let [xs (<! (api/req ajax.core/GET "/tags"))]
        (k (map obj->tag xs)))))
