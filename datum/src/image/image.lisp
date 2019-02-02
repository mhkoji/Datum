(defpackage :datum.image
  (:use :cl)
  (:export :image
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

(defun create-images (id-generator paths)
  (let ((image-ids (mapcar (lambda (path)
                             (datum.id:gen id-generator path))
                           paths)))
    (mapcar (lambda (id path)
              (datum.image.db:make-image :id id :path path))
            image-ids paths)))


(defstruct repository db)

(defun save-images (repos images)
  (datum.image.db:insert-images (repository-db repos) images)
  (values))

(defun load-images-by-ids (repos image-ids)
  (datum.image.db:select-images (repository-db repos) image-ids))

(defun delete-images (repos image-ids)
  (datum.image.db:delete-images (repository-db repos) image-ids)
  (values))
