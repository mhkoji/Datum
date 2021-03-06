(ns datum.tag)

(defrecord Tag [tag-id name])


(defrecord AttachedTagSet [tags])

(defn attach [attached-tag-set tag]
  (update attached-tag-set :tags conj tag))

(defn detach [attached-tag-set tag]
  (let [tag-id        (-> tag :tag-id)
        attached-tags (-> attached-tag-set :tags)]
    (AttachedTagSet. (remove #(= tag-id (-> % :tag-id))
                             attached-tags))))

(defn attached-p [attached-tag-set tag]
  (let [tag-id       (-> tag :tag-id)
        attached-ids (map :tag-id (-> attached-tag-set :tags))]
    (not (empty? (filter #(= % tag-id) attached-ids)))))

(defn attached-tags [attached-tag-set]
  (:tags attached-tag-set))
