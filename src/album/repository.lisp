(defpackage :datum.album.repository
  (:use :cl :datum.album.db)
  (:export :make-album
           :save-albums
           :load-albums
           :delete-albums))
(in-package :datum.album.repository)

(defstruct album
  id
  name
  updated-at

  thumbnail)

(defun album->album-row (album)
  (make-album-row :id (album-id album)
                  :name (album-name album)
                  :updated-at (album-updated-at album)))

(defun album->album-thumbnail-rows (album)
  (make-album-thumbnail-row
   :album-id (album-id album)
   :thumbnail-id (datum.album.thumbnail:thumbnail-id
                  (album-thumbnail album))))

(defun save-albums (db albums)
  (save-album-rows
   db (mapcar #'album->album-row albums))
  (save-album-thumbnail-rows
   db (mapcar #'album->album-thumbnail-rows albums)))


(defun album-row->album-args (row)
  (list :id (album-row-id row)
        :name (album-row-name row)
        :updated-at (album-row-updated-at row)))

(defun album-thumbnail-row->album-args (row id->thumbnail)
  (list :thumbnail (gethash (album-thumbnail-row-thumbnail-id row)
                            id->thumbnail)))

(defun load-albums (db ids thumbnail-repository)
  (let ((album-id->args (make-hash-table :test #'equal)))
    (let ((rows (select-album-rows db ids)))
      (dolist (row rows)
        (let ((album-id (album-row-id row))
              (sub-args (album-row->album-args row)))
          (alexandria:appendf (gethash album-id album-id->args) sub-args))))
    (let ((rows (select-album-thumbnail-rows db ids))
          (id->thumbnail (make-hash-table :test #'equal)))
      (let ((thumbnails
             (datum.album.thumbnail:load-by-ids
              thumbnail-repository
              (mapcar #'album-thumbnail-thumbnail-id
                      rows))))
        (dolist (thumbnail thumbnails)
          (setf (gethash (datum.album.thumbnail:thumbnail-id thumbnail)
                         id->thumbnail)
                thumbnail)))
      (dolist (row rows)
        (let ((album-id (album-thumbnail-row-album-id row))
              (sub-args (album-thumbnail-row->album-args row
                                                         id->thumbnail)))
          (alexandria:appendf (gethash album-id album-id->args) sub-args))))
    (mapcar (lambda (album-id)
              (apply #'make-album
                     (gethash album-id album-id->args)))
            ids)))


(defun delete-albums (db album-ids)
  (delete-album-rows db album-ids)
  (delete-album-thumbnail-rows db album-ids))
