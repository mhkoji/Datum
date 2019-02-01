(defpackage :datum.image
  (:use :cl)
  (:export :image
           :image-id
           :image-path

           :save-images
           :load-images-by-ids
           :delete-images))
(in-package :datum.image)

(defun create-images (id-generator paths)
  (let ((image-ids (mapcar (lambda (path)
                             (datum.id:gen id-generator path))
                           paths)))
    (mapcar (lambda (id path)
              (datum.image.db:make-image :id id :path path))
            image-ids paths)))

(defun save-images (paths db id-generator)
  (let ((images (create-images id-generator paths)))
    (datum.image.db:insert-images db images))
  (values))


(defun load-images-by-ids (db image-ids)
  (datum.image.db:select-images db image-ids))

(defun delete-images (db image-ids)
  (datum.image.db:delete-images db image-ids)
  (values))
