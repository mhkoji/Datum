(defpackage :datum.db
  (:use :cl)
  (:export :factory
           :connect
           :initialize
           :disconnect
           :execute-in-transaction
           :with-db))
(in-package :datum.db)

(defclass factory () ())

(defgeneric connect (factory))

(defgeneric initialize (db))

(defgeneric disconnect (db))

(defgeneric execute-in-transaction (db callback))


(defmacro with-db ((db factory) &body body)
  (let ((callback (gensym)))
    `(labels ((,callback (,db)
                (progn ,@body)))
       (let ((,db (connect ,factory)))
         (unwind-protect
              (execute-in-transaction ,db #',callback)
           (disconnect ,db))))))
