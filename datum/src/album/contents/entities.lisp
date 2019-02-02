(in-package :datum.album.contents)

(defmethod content-id ((entity datum.image:image))
  (datum.image:image-id entity))

(defmethod load-by-ids ((repos datum.image:repository) (content-ids list))
  (datum.image:load-images-by-ids repos content-ids))

(defmethod delete-by-ids ((repos datum.image:repository) (content-ids list))
  (datum.image:delete-images repos content-ids))
