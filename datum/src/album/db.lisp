(defpackage :datum.album.db
  (:use :cl)
  (:export :make-album-row
           :insert-album-rows
           :select-album-rows
           :select-album-ids
           :select-album-ids-by-like
           :delete-album-rows
           :album-row-id
           :album-row-name
           :album-row-updated-at

           :make-album-thumbnail-row
           :insert-album-thumbnail-rows
           :select-album-thumbnail-rows
           :delete-album-thumbnail-rows
           :album-thumbnail-row-album-id
           :album-thumbnail-row-thumbnail-id

           :load-albums-by-ids
           :select-album-ids
           :select-album-ids-by-like
           :save-albums
           :delete-albums))
(in-package :datum.album.db)

(defstruct album-row
  id
  name
  updated-at)

(defgeneric insert-album-rows (db rows))
(defgeneric select-album-rows (db album-ids))
(defgeneric select-album-ids (db offset count))
(defgeneric select-album-ids-by-like (db name))
(defgeneric delete-album-rows (db album-ids))


(defstruct album-thumbnail-row
  album-id
  thumbnail-id)

(defgeneric insert-album-thumbnail-rows (db rows))
(defgeneric select-album-thumbnail-rows (db album-ids))
(defgeneric delete-album-thumbnail-rows (db album-ids))

;;;

(defun album->album-row (album)
  (make-album-row :id (album-id album)
                  :name (album-name album)
                  :updated-at (album-updated-at album)))

(defun album->album-thumbnail-row (album)
  (make-album-thumbnail-row
   :album-id (album-id album)
   :thumbnail-id (datum.album.thumbnail:thumbnail-id
                  (album-thumbnail album))))


(defun save-albums (db albums)
  (insert-album-rows
   db (mapcar #'album->album-row albums))
  (insert-album-thumbnail-rows
   db (mapcar #'album->album-thumbnail-row
              (remove-if-not #'album-thumbnail albums))))


(defun make-id-hash-table ()
  (make-hash-table :test #'equal))

(defun id-gethash (id hash)
  (gethash (datum.id:to-string id) hash))

(defun (setf id-gethash) (val id hash)
  (setf (gethash (datum.id:to-string id) hash) val))

(defun load-albums-by-ids (db ids thumbnail-repository make-album-fn)
  (let ((album-id->args (make-id-hash-table)))
    (let ((rows (select-album-rows db ids)))
      (dolist (row rows)
        (let ((album-id (album-row-id row)))
          (alexandria:appendf (id-gethash album-id album-id->args)
           (list :id (album-row-id row)
                 :name (album-row-name row)
                 :updated-at (album-row-updated-at row))))))
    (let ((rows (select-album-thumbnail-rows db ids))
          (id->thumbnail (make-id-hash-table)))
      (let ((thumbnails (datum.album.thumbnail:load-by-ids
                         thumbnail-repository
                         (mapcar #'album-thumbnail-row-thumbnail-id rows))))
        (dolist (thumbnail thumbnails)
          (let ((thumbnail-id
                 (datum.album.thumbnail:thumbnail-id thumbnail)))
            (setf (id-gethash thumbnail-id id->thumbnail) thumbnail))))
      (dolist (row rows)
        (let ((album-id (album-thumbnail-row-album-id row))
              (thumbnail-id (album-thumbnail-row-thumbnail-id row)))
          (alexandria:appendf (id-gethash album-id album-id->args)
           (list :thumbnail (id-gethash thumbnail-id id->thumbnail))))))
    (mapcar (lambda (album-id)
              (funcall make-album-fn
                       (id-gethash album-id album-id->args)))
            ids)))


(defun delete-albums (db album-ids thumbnail-repository)
  (let ((thumbnail-ids (mapcar #'album-thumbnail-row-thumbnail-id
                               (select-album-thumbnail-rows db album-ids))))
    (datum.album.thumbnail:delete-by-ids thumbnail-repository thumbnail-ids))
  (delete-album-rows db album-ids)
  (delete-album-thumbnail-rows db album-ids))
