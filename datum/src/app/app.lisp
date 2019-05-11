(defpackage :datum.app
  (:use :cl)
  (:export :load-configure
           :make-configure
           :configure-thumbnail-root
           :configure-id-generator
           :with-container
           :initialize))
(in-package :datum.app)

(defstruct configure
  id-generator
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
                                      :db db)))
       ,@body)))


(defun initialize (conf)
  (with-container (container conf)
    (datum.container:initialize container)))
