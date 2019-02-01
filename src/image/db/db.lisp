(defpackage :datum.image.db
  (:use :cl)
  (:export :make-image
           :image-id
           :image-path

           :insert-images
           :select-images
           :delete-images))
(in-package :datum.image.db)

(defstruct image id path)

(defgeneric insert-images (db images))
(defgeneric select-images (db image-ids))
(defgeneric delete-images (db image-ids))
