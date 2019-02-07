(defpackage :datum.album
  (:use :cl)
  (:export :album
           :album-id
           :album-name
           :album-thumbnail
           :album-pictures

           :album-cover
           :cover
           :cover-album-id
           :cover-name
           :cover-thumbnail

           :album-overview
           :overview
           :overview-album-id
           :overview-name
           :overview-pictures

           :make-pictures-appending
           :append-album-pictures

           :make-loader
           :loader
           :load-albums-by-ids
           :load-albums-by-range

           :delete-albums

           :make-source
           :create-albums
           :save-albums)
  (:import-from :datum.album.repository
                :album
                :album-id
                :album-name
                :album-thumbnail))
(in-package :datum.album)


;;; Pictures
(defmethod datum.album.pictures:album-id ((album album))
  (album-id album))

(defun album-pictures (album db entity-repository)
  (datum.album.pictures:load-by-album db entity-repository album))


(defstruct pictures-appending album entities)

(defun append-album-pictures (pictures-appendings db)
  (dolist (appending pictures-appendings)
    (let ((album (pictures-appending-album appending))
          (entities (pictures-appending-entities appending)))
      (datum.album.pictures:append-to-album db album entities))))


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


(defun delete-albums (db album-ids)
  (datum.album.pictures:delete-by-album-ids db album-ids)
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



(defstruct cover album-id name thumbnail)

(defun album-cover (album)
  (make-cover :album-id (album-id album)
              :name (album-name album)
              :thumbnail (album-thumbnail album)))


(defstruct overview album-id name pictures)

(defun album-overview (album db entity-repository)
  (make-overview :album-id (album-id album)
                 :name (album-name album)
                 :pictures (album-pictures album db entity-repository)))
