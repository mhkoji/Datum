(defpackage :datum.album.thumbnail
  (:use :cl)
  (:export :thumbnail-id
           :load-by-ids))
(in-package :datum.album.thumbnail)

(defgeneric thumbnail-id (th))

(defgeneric load-by-ids (thumbnail-repository ids))

