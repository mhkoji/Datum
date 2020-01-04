(defpackage :datum.app
  (:use :cl)
  (:export :load-configure
           :make-configure
           :configure-thumbnail-root
           :with-container
           :initialize))
(in-package :datum.app)

(defstruct configure
  db-factory
  thumbnail-root)

(defun load-configure (&optional (path (merge-pathnames
                                        ".datum.config.lisp"
                                        (user-homedir-pathname))))
  (when (cl-fad:file-exists-p path)
    (with-open-file (in path) (read in))))


(defmacro with-container ((container conf) &body body)
  `(datum.db:with-db (db (configure-db-factory ,conf))
     (let ((,container (make-instance 'datum.container:container
                                      :thumbnail-dir
                                      (configure-thumbnail-root ,conf)
                                      :db db)))
       ,@body)))


(defun initialize (conf)
  (with-container (container conf)
    (datum.container:initialize container)))
