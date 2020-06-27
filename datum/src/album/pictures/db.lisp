(defpackage :datum.album.pictures.db
  (:use :cl)
  (:export :insert-pictures
           :select-pictures
           :delete-pictures))
(in-package :datum.album.pictures.db)

(defgeneric insert-pictures (db album-id picture-ids))

(defgeneric select-pictures (db album-ids))

(defgeneric delete-pictures (db album-ids))
