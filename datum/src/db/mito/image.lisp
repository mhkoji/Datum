(in-package :datum.db.mito)

(defmethod datum.image.db:insert-images ((db <dbi-connection>)
                                         (images list))
  (when images
    (let ((sql (conc-strings
                "INSERT INTO image"
                " (image_id, path)"
                " VALUES"
                (format nil "~{~A~^,~}"
                        (loop repeat (length images) collect "(?,?)")))))
      (execute db sql (alexandria:mappend
                       (lambda (image)
                         (list (datum.id:to-string
                                (datum.image:image-id image))
                               (datum.image:image-path image)))
                       images)))))

(defmethod datum.image.db:select-images ((db <dbi-connection>)
                                         (image-ids list))
  (when image-ids
    (let ((sql (conc-strings
                "SELECT *"
                " FROM"
                "  image"
                " WHERE"
                "  image_id in"
                " ("
                (format nil "~{~A~^,~}"
                        (loop repeat (length image-ids) collect "?"))
                " )")))
      (let ((plist-rows
             (query db sql (mapcar #'datum.id:to-string image-ids))))
        (mapcar (lambda (plist)
                  (datum.image:make-image
                   :id
                   (datum.id:from-string (getf plist :|image_id|))
                   :path (getf plist :|path|)))
                plist-rows)))))

(defmethod datum.image.db:delete-images ((db <dbi-connection>)
                                         (image-ids list))
  (when image-ids
    (let ((sql (conc-strings
                "DELETE"
                " FROM"
                "  image"
                " WHERE"
                "  image_id in"
                " ("
                (format nil "~{~A~^,~}"
                        (loop repeat (length image-ids) collect "?"))
                " )")))
      (execute db sql (mapcar #'datum.id:to-string image-ids)))))
