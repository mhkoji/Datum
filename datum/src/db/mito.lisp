(defpackage :datum.db.mito
  (:use :cl :datum.db)
  (:export :mito-factory
           :listed))
(in-package :datum.db.mito)

(defclass mito-factory ()
  ((args :initarg :args
         :reader args)))

(defclass listed () ())

(defun list-mito-table-classes ()
  (c2mop:class-direct-subclasses (find-class 'listed)))

(defmethod connect ((factory mito-factory))
  (apply #'dbi:connect (args factory)))

(defmethod initialize ((db dbi.driver:<dbi-connection>))
  (dolist (class (list-mito-table-classes))
    (mito:ensure-table-exists class)))

(defmethod disconnect ((db dbi.driver:<dbi-connection>))
  (dbi:disconnect db))

(defmethod execute-in-transaction ((db dbi.driver:<dbi-connection>) callback)
  (dbi:with-transaction db
    (let ((mito:*connection* db))
      (funcall callback db))))
