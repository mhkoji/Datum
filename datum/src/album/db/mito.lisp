(defpackage :datum.album.db.mito
  (:use :cl :datum.album.db)
  (:export :album
           :album-thumbnail)
  (:import-from :dbi.driver
                :<dbi-connection>))
(in-package :datum.album.db.mito)

(defclass album ()
  ((album-id :col-type (:varchar 256)
             :accessor album-id)
   (name :col-type (:varchar 256)
         :accessor album-name)
   (updated-at :col-type :timestamp
               :accessor album-updated-at))
  (:metaclass mito:dao-table-class)
  (:record-timestamps nil))

(defmethod insert-album-rows ((db <dbi-connection>)
                              (rows list))
  (dolist (row rows)
    (mito:create-dao 'album
                     :album-id (album-row-id row)
                     :name (album-row-name row)
                     :updated-at (album-row-updated-at row))))

(defmethod select-album-rows ((db <dbi-connection>)
                              (album-ids list))
  (let ((objects (mito:select-dao 'album
                   (sxql:where (:in :album-id album-ids)))))
    (mapcar (lambda (obj)
              (make-album-row
               :id (album-id obj)
               :name (album-name obj)
               :updated-at (album-updated-at obj)))
            objects)))

(defmethod select-album-ids ((db <dbi-connection>)
                             offset count)
  (mapcar #'album-id
          (mito:select-dao 'album
            (sxql:order-by (:desc :updated_at))
            (sxql:limit offset count))))


(defmethod delete-album-rows ((db <dbi-connection>) (album-ids list))
  (dolist (album-id album-ids)
    (mito:delete-by-values 'album :album-id album-id)))


(defclass album-thumbnail ()
  ((album-id :col-type (:varchar 256)
             :accessor album-id)
   (thumbnail-id :col-type (:varchar 256)
                 :accessor album-thumbnail-id))
  (:metaclass mito:dao-table-class))

(defmethod insert-album-thumbnail-rows ((db <dbi-connection>)
                                        (rows list))
  (dolist (row rows)
    (mito:create-dao 'album-thumbnail
                     :album-id (album-thumbnail-row-album-id row)
                     :thumbnail-id (album-thumbnail-row-thumbnail-id row))))

(defmethod select-album-thumbnail-rows ((db <dbi-connection>)
                                        (album-ids list))
  (let ((objects (mito:select-dao 'album-thumbnail
                   (sxql:where (:in :album-id album-ids)))))
    (mapcar (lambda (obj)
              (make-album-thumbnail-row
               :album-id (album-id obj)
               :thumbnail-id (album-thumbnail-id obj)))
            objects)))

(defmethod delete-album-thumbnail-rows ((db <dbi-connection>)
                                        (album-ids list))
  (dolist (album-id album-ids)
    (mito:delete-by-values 'album-thumbnail :album-id album-id)))
