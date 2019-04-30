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


(defclass container (datum.album:container
                     datum.image:container)
  ((db :initarg :db
       :accessor container-db)))

(defmacro with-container ((container conf) &body body)
  `(datum.db:with-db (db (configure-db-factory ,conf))
     (let ((,container (make-instance 'container :db db)))
       ,@body)))

(defun make-image-repository (c)
  (make-instance 'datum.image:repository :db (container-db c)))


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
  (alexandria:alist-hash-table
   (list (cons :album (make-instance 'datum.album:loader
                                     :db (container-db c)
                                     :thumbnail-repository
                                     (make-image-repository c)))
         (cons :image (make-image-repository c)))))

(defmethod datum.tag:load-contents ((loader hash-table)
                                    type
                                    content-ids)
  (datum.tag:load-contents (gethash type loader) type content-ids))


(defmethod datum.access-log:container-db ((c container))
  (container-db c))

(defmethod datum.access-log:container-resource-loader ((c container))
  (make-instance 'datum.album:loader
                 :db (container-db c)
                 :thumbnail-repository (make-image-repository c)))
