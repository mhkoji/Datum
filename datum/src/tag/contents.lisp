(in-package :datum.tag)

(defmethod content-id ((c datum.album:album))
  (datum.album:album-id c))

(defmethod content-type ((c datum.album:album))
  :album)

(defmethod load-contents ((loader datum.album:loader)
                          (type (eql :album))
                          (content-ids list))
  (datum.album:load-albums-by-ids loader content-ids))
