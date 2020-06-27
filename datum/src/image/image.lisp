(defpackage :datum.image
  (:use :cl)
  (:export :make-image
           :image
           :image-id
           :image-path

           :repository
           :container
           :container-db
           :load-images-by-ids
           :delete-images
           :create-images
           :save-images))
(in-package :datum.image)

(defstruct image id path)

(defun create-images (paths)
  (let ((image-ids (mapcar #'datum.id:gen paths)))
    (mapcar (lambda (id path)
              (make-image :id id :path path))
            image-ids paths)))


(defclass repository ()
  ((db
    :initarg :db
    :reader repository-db)))

(defun load-images-by-ids (repos image-ids)
  (datum.image.db:select-images (repository-db repos) image-ids))

(defun delete-images (repos image-ids)
  (datum.image.db:delete-images (repository-db repos) image-ids)
  (values))

(defun save-images (repos images)
  (datum.image.db:insert-images (repository-db repos) images)
  (values))


(defclass container () ())

(defgeneric container-db (c))

(defmethod repository-db ((c container))
  (container-db c))
