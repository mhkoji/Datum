(in-package :datum.tag)

(defmethod content-id ((c datum.album:album))
  (datum.album:album-id c))

(defmethod content-type ((c datum.album:album))
  :album)
