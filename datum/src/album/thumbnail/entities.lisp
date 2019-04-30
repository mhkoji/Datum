(in-package :datum.album.thumbnail)

(defmethod thumbnail-id ((th datum.image:image))
  (datum.image:image-id th))

(defmethod load-by-ids ((repos datum.image:repository) (thumbnail-ids list))
  (datum.image:load-images-by-ids repos thumbnail-ids))

(defmethod delete-by-ids ((repos datum.image:repository) (thumbnail-ids list))
  (datum.image:delete-images repos thumbnail-ids))
