(defpackage :datum.db.mito.image
  (:use :cl)
  (:export :image)
  (:import-from :dbi.driver
                :<dbi-connection>))
(in-package :datum.db.mito.image)

(defclass image (datum.db.mito:listed)
  ((image-id :col-type (:varchar 256)
             :accessor image-id)
   (path :col-type (:varchar 256)
         :accessor image-path))
  (:metaclass mito:dao-table-class))

(defmethod datum.image.db:insert-images ((db <dbi-connection>)
                                         (images list))
  (dolist (image images)
    (mito:create-dao 'image
                     :image-id (datum.id:to-string
                                (datum.image:image-id image))
                     :path (datum.image:image-path image))))

(defmethod datum.image.db:select-images ((db <dbi-connection>)
                                         (image-ids list))
  (let ((objects
         (mito:select-dao 'image
           (sxql:where
            (:in :image-id (mapcar #'datum.id:to-string image-ids))))))
    (mapcar (lambda (obj)
              (datum.image:make-image
               :id (datum.id:from-string (image-id obj))
               :path (image-path obj)))
            objects)))

(defmethod datum.image.db:delete-images ((db <dbi-connection>)
                                         (image-ids list))
  (dolist (image-id image-ids)
    (mito:delete-by-values 'image
                           :image-id (datum.id:to-string image-id))))
