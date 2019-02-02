(defpackage :datum.image.db.mito
  (:use :cl :datum.image.db)
  (:export :%image)
  (:import-from :dbi.driver
                :<dbi-connection>))
(in-package :datum.image.db.mito)

(defclass %image ()
  ((image-id :col-type (:varchar 256)
             :accessor %image-id)
   (path :col-type (:varchar 256)
         :accessor %image-path))
  (:metaclass mito:dao-table-class))

(defmethod insert-images ((db <dbi-connection>) (images list))
  (dolist (image images)
    (mito:create-dao '%image
                     :image-id (image-id image)
                     :path (image-path image))))

(defmethod select-images ((db <dbi-connection>) (image-ids list))
  (let ((objects (mito:select-dao '%image
                   (sxql:where (:in :image-id image-ids)))))
    (mapcar (lambda (obj)
              (make-image :id (%image-id obj) :path (%image-path obj)))
            objects)))

(defmethod delete-images ((db <dbi-connection>) (image-ids list))
  (dolist (image-id image-ids)
    (mito:delete-by-values '%image :image-id image-id)))
