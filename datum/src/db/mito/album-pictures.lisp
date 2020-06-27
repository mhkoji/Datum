(defpackage :datum.db.mito.album-pictures
  (:use :cl :datum.album.pictures.db)
  (:export :album-picture)
  (:import-from :dbi.driver
                :<dbi-connection>))
(in-package :datum.db.mito.album-pictures)

(defclass album-picture (datum.db.mito:listed)
  ((album-id :col-type (:varchar 256)
             :accessor album-id)
   (picture-id :col-type (:varchar 256)
               :accessor album-picture-id))
  (:metaclass mito:dao-table-class))

(defmethod insert-pictures ((db <dbi-connection>)
                            (album-id t)
                            (picture-ids list))
  (dolist (picture-id picture-ids)
    (mito:create-dao 'album-picture
                     :album-id (datum.id:to-string album-id)
                     :picture-id (datum.id:to-string picture-id))))

(defmethod select-pictures ((db <dbi-connection>) (album-ids list))
  (let ((objects (mito:select-dao 'album-picture
                   (sxql:where (:in :album-id
                                    (mapcar #'datum.id:to-string
                                            album-ids))))))
    (mapcar #'album-picture-id objects)))

(defmethod delete-pictures ((db <dbi-connection>) (album-ids list))
  (dolist (album-id album-ids)
    (mito:delete-by-values 'album-picture
                           :album-id (datum.id:to-string album-id))))
