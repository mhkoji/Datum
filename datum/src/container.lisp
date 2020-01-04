(defpackage :datum.container
  (:use :cl)
  (:export :container
           :initialize
           :create-thumbnail-file))
(in-package :datum.container)

(defclass container (datum.album:container
                     datum.image:container)
  ((db :initarg :db
       :accessor container-db)
   (thumbnail-dir :initarg :thumbnail-dir
                  :reader container-thumbnail-dir)))

(defun initialize (container)
  (datum.db:initialize (container-db container)))


(defun make-thumbnail-path (container image-path)
  (format nil "~Athumbnail$~A"
          (container-thumbnail-dir container)
          (cl-ppcre:regex-replace-all "/" image-path "$")))

(defun create-thumbnail-file (container image-path)
  (log:debug "Creating thumbnail for: ~A" image-path)
  (let ((thumbnail-path (make-thumbnail-path container image-path)))
    (datum.fs.thumbnail:ensure-exists thumbnail-path image-path)
    thumbnail-path))


(defun make-image-repository (c)
  (make-instance 'datum.image:repository :db (container-db c)))


(defmethod datum.image:container-db ((c container))
  (container-db c))


(defmethod datum.album:container-db ((c container))
  (container-db c))

(defmethod datum.album:container-thumbnail-repository ((c container))
  (make-image-repository c))

(defmethod datum.album:container-image-repository ((c container))
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
