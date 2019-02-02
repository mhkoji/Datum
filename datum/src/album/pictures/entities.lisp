(in-package :datum.album.pictures)

(defmethod picture-id ((entity datum.image:image))
  (datum.image:image-id entity))

(defmethod load-by-ids ((loader datum.image:repository) (picture-ids list))
  (datum.image:load-images-by-ids loader picture-ids))
