(defpackage :datum.album.pictures
  (:use :cl)
  (:export :album-id
           :save-pictures
           :load-by-album
           :delete-by-album-ids
           :append-to-album))
(in-package :datum.album.pictures)

;;; In this context, an album is an object which has its unique id.
(defgeneric album-id (album))

(defun load-by-album (db image-repos album)
  (let ((image-ids (datum.album.pictures.db:select-pictures
                    db (list (album-id album)))))
    (datum.image:load-images-by-ids image-repos image-ids)))

(defun delete-by-album-ids (db image-repos album-ids)
  (datum.album.pictures.db:delete-pictures db album-ids)
  (let ((image-ids (datum.album.pictures.db:select-pictures
                    db album-ids)))
    (datum.image:delete-images image-repos image-ids)))

(defun append-to-album (db album images)
  (let ((album-id (album-id album))
        (image-ids (mapcar #'datum.image:image-id images)))
    (datum.album.pictures.db:insert-pictures db album-id image-ids)))
