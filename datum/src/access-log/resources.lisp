(in-package :datum.access-log)

(defmethod resource-id ((resource datum.album:album))
  (datum.album:album-id resource))

(defmethod resource-type ((resource datum.album:album))
  :album)

(defmethod load-resources-by-ids ((loader datum.album:loader)
                                  (type (eql :album))
                                  (resource-ids list))
  (datum.album:load-albums-by-ids loader resource-ids))
