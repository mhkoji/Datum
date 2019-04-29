(defpackage :datum.album.pictures
  (:use :cl)
  (:export :album-id
           :save-pictures
           :load-by-album
           :delete-by-album-ids
           :append-to-album))
(in-package :datum.album.pictures)

;;; A picture is an entity who plays the following roles.
(defgeneric picture-id (entity))

(defgeneric load-by-ids (entity-repos picture-ids))

(defgeneric delete-by-ids (entity-repos picture-ids))


;;; In this context, an album is an object which has its unique id.
(defgeneric album-id (album))

(defun load-by-album (db entity-repos album)
  (let ((picture-ids (datum.album.pictures.db:select-pictures
                      db (list (album-id album)))))
    (load-by-ids entity-repos picture-ids)))

(defun delete-by-album-ids (db entity-repos album-ids)
  (let ((picture-ids (datum.album.pictures.db:select-pictures
                      db album-ids)))
    (datum.album.pictures.db:delete-pictures db album-ids)
    (delete-by-ids entity-repos picture-ids)))

(defun append-to-album (db album entities)
  (let ((album-id (album-id album))
        (picture-ids (mapcar #'picture-id entities)))
    (datum.album.pictures.db:insert-pictures db album-id picture-ids)))
