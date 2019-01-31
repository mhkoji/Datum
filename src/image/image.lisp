(defpackage :datum.image
  (:use :cl)
  (:shadow :delete)
  (:export :select
           :insert
           :delete
           :make-image
           :image-id
           :image-path
           :save-images
           :load-by-ids
           :delete-by-ids))
(in-package :datum.image)

(defstruct image id path)


(defgeneric select (db ids))

(defgeneric insert (db images))

(defgeneric delete (db ids))


(defun save-images (db images)
  (insert db images))

(defun load-by-ids (db ids)
  (select db ids))

(defun delete-by-ids (db ids)
  (delete db ids))
