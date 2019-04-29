(defpackage :datum.container
  (:use :cl)
  (:export :make-configure
           :load-configure
           :configure-thumbnail-root
           :configure-id-generator
           :with-container
           :container-db))
(in-package :datum.container)

(defstruct configure
  id-generator
  db-factory
  thumbnail-root)

(defun load-configure (&optional (path (merge-pathnames
                                        ".datum.config.lisp"
                                        (user-homedir-pathname))))
  (when (cl-fad:file-exists-p path)
    (with-open-file (in path) (read in))))


(defstruct container db)

(defmacro with-container ((container conf) &body body)
  `(datum.db:with-db (db (configure-db-factory ,conf))
     (let ((,container (make-container :db db)))
       ,@body)))

(defun make-image-repository (c)
  (datum.image:make-repository :db (container-db c)))


(defmethod datum.image:container-db ((c container))
  (container-db c))


(defmethod datum.album:container-db ((c container))
  (container-db c))

(defmethod datum.album:container-thumbnail-repository ((c container))
  (make-image-repository c))

(defmethod datum.album:container-entity-repository ((c container))
  (make-image-repository c))


(defmethod datum.tag:container-db ((c container))
  (container-db c))

(defmethod datum.tag:container-content-loader ((c container))
  c)

(defmethod datum.tag:load-contents ((loader container)
                                    (type (eql :album))
                                    (content-ids list))
  (datum.album:load-albums-by-ids loader content-ids))

(defmethod datum.tag:load-contents ((loader hash-table)
                                    type
                                    content-ids)
  (datum.tag:load-contents (gethash type loader) type content-ids))
