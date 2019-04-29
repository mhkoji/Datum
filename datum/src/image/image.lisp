(defpackage :datum.image
  (:use :cl)
  (:export :container-db

           :image
           :image-id
           :image-path

           :repository
           :make-repository
           :create-images
           :save-images
           :load-images-by-ids
           :delete-images)
  (:import-from :datum.image.db
                :image
                :image-id
                :image-path))
(in-package :datum.image)

(defgeneric container-db (c))

(defun create-images (id-generator paths)
  (let ((image-ids (mapcar (lambda (path)
                             (datum.id:gen id-generator path))
                           paths)))
    (mapcar (lambda (id path)
              (datum.image.db:make-image :id id :path path))
            image-ids paths)))

(defstruct repository db)

(defmethod container-db ((c repository))
  (repository-db c))

(defun save-images (container images)
  (datum.image.db:insert-images (container-db container) images)
  (values))

(defun load-images-by-ids (container image-ids)
  (datum.image.db:select-images (container-db container) image-ids))

(defun delete-images (container image-ids)
  (datum.image.db:delete-images (container-db container) image-ids)
  (values))
