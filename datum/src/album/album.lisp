(defpackage :datum.album
  (:use :cl)
  (:export :loader
           :load-albums-by-ids
           :load-albums-by-range
           :search-albums

           :container
           :container-db
           :container-thumbnail-repository
           :container-image-repository
           :save-albums
           :delete-albums

           :album
           :album-id
           :album-name
           :album-thumbnail

           :album-pictures
           :make-pictures-appending
           :append-album-pictures

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

           :make-source
           :create-albums
           :save-albums))
(in-package :datum.album)

(defstruct album
  id
  name
  updated-at
  thumbnail)

(defstruct source name thumbnail updated-at)

(defun create-albums (sources)
  (let ((album-ids (mapcar (lambda (source)
                             (datum.id:gen (source-name source)))
                           sources)))
    (mapcar (lambda (album-id source)
              (make-album
               :id album-id
               :name (source-name source)
               :updated-at (source-updated-at source)
               :thumbnail (source-thumbnail source)))
            album-ids sources)))

;;;

(defclass loader ()
  ((db
    :initarg :db
    :reader loader-db)
   (thumbnail-repository
    :initarg :thumbnail-repository
    :reader loader-thumbnail-repository)))

(defun load-albums-by-ids (loader ids)
  (datum.album.db:load-albums-by-ids (loader-db loader)
                                     ids
                                     (loader-thumbnail-repository loader)
                                     (lambda (args)
                                       (apply #'make-album args))))

(defun load-albums-by-range (loader offset count)
  (load-albums-by-ids
   loader
   (datum.album.db:select-album-ids (loader-db loader) offset count)))

(defun search-albums (loader name)
  (load-albums-by-ids
   loader
   (datum.album.db:select-album-ids-by-like (loader-db loader) name)))


;;;

(defclass container () ())
(defgeneric container-db (c))
(defgeneric container-thumbnail-repository (c))
(defgeneric container-image-repository (c))

(defmethod loader-db ((c container))
  (container-db c))

(defmethod loader-thumbnail-repository ((c container))
  (container-thumbnail-repository c))

(defun save-albums (container albums)
  (datum.album.db:save-albums (container-db container) albums))


(defun delete-albums (container album-ids)
  (let ((db (container-db container))
        (image-repos (container-image-repository container))
        (thumbnail-repos (container-thumbnail-repository container)))
    (datum.album.pictures:delete-by-album-ids db image-repos album-ids)
    (datum.album.db:delete-albums db album-ids thumbnail-repos)))



;;; Pictures
(defmethod datum.album.pictures:album-id ((album album))
  (album-id album))

(defstruct pictures-appending album entities)

(defun append-album-pictures (container pictures-appendings)
  (let ((db (container-db container)))
    (dolist (appending pictures-appendings)
      (let ((album (pictures-appending-album appending))
            (entities (pictures-appending-entities appending)))
        (datum.album.pictures:append-to-album db album entities)))))

(defun album-pictures (container album)
  (datum.album.pictures:load-by-album (container-db container)
                                      (container-image-repository container)
                                      album))

;;; Others
(defstruct cover album-id name thumbnail)

(defun album-cover (album)
  (make-cover :album-id (album-id album)
              :name (album-name album)
              :thumbnail (album-thumbnail album)))

(defstruct overview album-id name pictures)

(defun album-overview (album container)
  (make-overview :album-id (album-id album)
                 :name (album-name album)
                 :pictures (album-pictures container album)))

