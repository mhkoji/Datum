(defpackage :datum.album.contents.db.mito
  (:use :cl :datum.album.contents.db)
  (:import-from :dbi.driver
                :<dbi-connection>))
(in-package :datum.album.contents.db.mito)

(defclass album-content ()
  ((album-id :col-type (:varchar 256)
             :accessor album-id)
   (content-id :col-type (:varchar 256)
               :accessor album-content-id))
  (:metaclass mito:dao-table-class))

(defmethod insert-contents ((db <dbi-connection>)
                            (album-id t)
                            (content-ids list))
  (dolist (content-id content-ids)
    (mito:create-dao 'album-content
                     :album-id album-id
                     :content-id content-id)))

(defmethod select-contents ((db <dbi-connection>) (album-ids list))
  (let ((objects (mito:select-dao 'album-content
                   (sxql:where (:in :album-id album-ids)))))
    (mapcar #'album-content-id objects)))

(defmethod delete-contents ((db <dbi-connection>) (album-ids list))
  (dolist (album-id album-ids)
    (mito:delete-by-values 'album-content :album-id album-id)))
