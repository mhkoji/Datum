(in-package :datum.album.contents)

(defmethod content-id ((content datum.image:image))
  (datum.image:image-id content))

(defmethod load-by-ids ((repos datum.image:repository) (content-ids list))
  (datum.image:load-images-by-ids repos content-ids))

(defmethod delete-by-ids ((repos datum.image:repository) (content-ids list))
  (datum.image:delete-images repos content-ids))
