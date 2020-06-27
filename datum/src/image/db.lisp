(defpackage :datum.image.db
  (:use :cl)
  (:export :insert-images
           :select-images
           :delete-images))
(in-package :datum.image.db)

(defgeneric insert-images (db images))
(defgeneric select-images (db image-ids))
(defgeneric delete-images (db image-ids))
