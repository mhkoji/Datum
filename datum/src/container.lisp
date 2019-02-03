(defpackage :datum.container
  (:use :cl)
  (:export :make-configure
           :load-configure

           :with-container
           :get-db
           :get-id-generator
           :get-image-repository
           :get-album-loader
           :get-thumbnail-file-fn))
(in-package :datum.container)

(defstruct configure
  id-generator
  db-factory
  thumbnail-root)

(defun load-configure (&optional (path (merge-pathnames
                                        ".datum.config.lisp"
                                        (user-homedir-pathname))))
  (when (cl-fad:file-exists-p path)
    (with-open-file (in path) (read in))))


(defstruct container db id-generator thumbnail-root)

(defun get-db (c)
  (container-db c))

(defun get-id-generator (c)
  (container-id-generator c))

(defun get-image-repository (c)
  (datum.image:make-repository :db (get-db c)))

(defun get-album-loader (c)
  (datum.album:make-loader
   :db (get-db c)
   :thumbnail-repository (get-image-repository c)))

(defun get-thumbnail-file-fn (c)
  (labels ((make-thumbnail-path (source-path)
             (format nil "~Athumbnail$~A"
                     (container-thumbnail-root c)
                     (cl-ppcre:regex-replace-all "/" source-path "$")))
           (make-thumbnail-file (source-path)
             (log:debug "Creating thumbnail for: ~A" source-path)
             (let ((thumbnail-path (make-thumbnail-path source-path)))
               (datum.fs.thumbnail:ensure-exists thumbnail-path source-path)
               thumbnail-path)))
    #'make-thumbnail-file))


(defmacro with-container ((container conf) &body body)
  `(datum.db:with-db (db (configure-db-factory ,conf))
     (let ((,container
            (make-container
             :db db
             :id-generator (configure-id-generator ,conf)
             :thumbnail-root (configure-thumbnail-root ,conf))))
       ,@body)))
