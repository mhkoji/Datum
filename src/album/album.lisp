(defpackage :datum.album
  (:use :cl))
(in-package :datum.album)

(defstruct album
  id
  thumbnail
  updated-at)


;;; Contents
(defmethod datum.album.contents:album-id ((album album))
  (album-id album))

(defun album-contents (album db content-repository)
  (datum.album.contents:load-by-album album db content-repository))

(defun append-album-contents (album db contents)
  (datum.album.contents:append-to-album db album contents))
