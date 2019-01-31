(defpackage :datum.album.contents
  (:use :cl)
  (:export :album-id
           :load-by-album
           :append-to-album))
(in-package :datum.album.contents)

(defgeneric content-id (content))

(defgeneric load-by-ids (content-repository content-ids))


;;; In this context, an album is an object which has its unique id.
(defgeneric album-id (album))

(defun load-by-album (db content-repository album)
  (let ((content-ids (datum.album.contents.db:select db (album-id album))))
    (load-by-ids content-repository content-ids)))

(defun append-to-album (db album contents)
  (let ((album-id (album-id album))
        (content-ids (mapcar #'content-id contents)))
    (datum.album.contents.db:insert db album-id content-ids)))
