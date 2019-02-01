(defpackage :datum.album
  (:use :cl)
  (:export :album-id
           :album-name
           :album-thumbnail
           :album-contents

           :make-contents-appending
           :append-album-contents

           :make-source
           :create-albums
           :save-albums

           :make-loader
           :laod-albums-by-ids
           :load-albums-by-range

           :delete-albums)
  (:import-from :datum.album.repository
                :album
                :album-id
                :album-name
                :album-thumbnail))
(in-package :datum.album)

;;; Contents
(defmethod datum.album.contents:album-id ((album album))
  (album-id album))

(defun album-contents (album db content-repository)
  (datum.album.contents:load-by-album db content-repository album))


(defstruct contents-appending album contents)

(defun append-album-contents (db contents-appendings)
  (dolist (appending contents-appendings)
    (let ((album (contents-appending-album appending))
          (contents (contents-appending-contents appending)))
      (datum.album.contents:append-to-album db album contents))))


;;; Album CRUD
(defstruct loader db thumbnail-repository)

(defun load-albums-by-ids (loader ids)
  (datum.album.repository:load-albums
   (loader-db loader)
   ids
   (loader-thumbnail-repository loader)))

(defun load-albums-by-range (loader offset count)
  (datum.album.repository:load-albums
   (loader-db loader)
   (datum.album.db:select-album-ids (loader-db loader) offset count)
   (loader-thumbnail-repository loader)))


(defun delete-albums (db content-repository album-ids)
  (datum.album.contents:delete-by-album-ids db content-repository album-ids)
  (datum.album.repository:delete-albums db album-ids))


(defstruct source name thumbnail updated-at)

(defun create-albums (id-generator sources)
  (let ((album-ids (mapcar (lambda (source)
                             (datum.id:gen id-generator
                                           (source-name source)))
                           sources)))
    (mapcar (lambda (album-id source)
              (datum.album.repository:make-album
               :id album-id
               :name (source-name source)
               :updated-at (source-updated-at source)
               :thumbnail (source-thumbnail source)))
            album-ids sources)))


(defun save-albums (db albums)
  (datum.album.repository:save-albums db albums))
