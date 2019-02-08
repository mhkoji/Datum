(defpackage :datum.db.mito
  (:use :cl :datum.db)
  (:export :mito-factory))
(in-package :datum.db.mito)

(defclass mito-factory ()
  ((args :initarg :args
         :reader args)))

(defmethod connect ((factory mito-factory))
  (apply #'mito:connect-toplevel (args factory)))

(defmethod initialize ((db dbi.driver:<dbi-connection>))
  (dolist (sym (list 'datum.album.db.mito:album
                     'datum.album.db.mito:album-thumbnail
                     'datum.album.pictures.db.mito:album-picture
                     'datum.image.db.mito:%image
                     'datum.tag.db.mito:tag
                     'datum.tag.db.mito:tag-content))
    (mito:ensure-table-exists sym)))

(defmethod disconnect ((db dbi.driver:<dbi-connection>))
  (dbi:disconnect db))

(defmethod execute-in-transaction ((db dbi.driver:<dbi-connection>) callback)
  (dbi:with-transaction db
    (funcall callback db)))
