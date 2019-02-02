(defpackage :datum.album.thumbnail
  (:use :cl)
  (:export :thumbnail-id
           :load-by-ids))
(in-package :datum.album.thumbnail)

;;; A thumbnail is an entity who plays the following roles.
(defgeneric thumbnail-id (thumbnail))

(defgeneric load-by-ids (thumbnail-repository thumbnail-ids))
