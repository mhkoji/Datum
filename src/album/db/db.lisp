(defpackage :datum.album.db
  (:use :cl)
  (:export :make-album-row
           :insert-album-rows
           :select-album-rows
           :select-album-ids
           :delete-album-rows
           :album-row-id
           :album-row-name
           :album-row-updated-at

           :make-album-thumbnail-row
           :insert-album-thumbnail-rows
           :select-album-thumbnail-rows
           :delete-album-thumbnail-rows
           :album-thumbnail-row-album-id
           :album-thumbnail-row-thumbnail-id))
(in-package :datum.album.db)

(defstruct album-row
  id
  name
  updated-at)

(defgeneric insert-album-rows (db rows))
(defgeneric select-album-rows (db album-ids))
(defgeneric select-album-ids (db offset count))
(defgeneric delete-album-rows (db album-ids))


(defstruct album-thumbnail-row
  album-id
  thumbnail-id)

(defgeneric insert-album-thumbnail-rows (db rows))
(defgeneric select-album-thumbnail-rows (db album-ids))
(defgeneric delete-album-thumbnail-rows (db album-ids))
