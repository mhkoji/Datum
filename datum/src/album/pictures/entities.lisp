(in-package :datum.album.pictures)

(defmethod picture-id ((entity datum.image:image))
  (datum.image:image-id entity))

(defmethod load-by-ids ((repos datum.image:repository) (picture-ids list))
  (datum.image:load-images-by-ids repos picture-ids))

(defmethod delete-by-ids ((repos datum.image:repository) (picture-ids list))
  (datum.image:delete-images repos picture-ids))
