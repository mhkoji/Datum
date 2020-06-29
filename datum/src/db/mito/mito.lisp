(defpackage :datum.db.mito
  (:use :cl :datum.db)
  (:export :mito-factory)
  (:import-from :dbi.driver
                :<dbi-connection>))
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


(defmacro conc-strings (&rest strings)
  `(concatenate 'string ,@strings))

(defun format-datetime (datetime)
  (local-time:format-timestring nil datetime
   :format '((:year 4) #\- (:month 2) #\- (:day 2)
             #\Space
             (:hour 2) #\: (:min 2) #\: (:sec 2) #\. (:nsec))))

(defun parse-datetime (string)
  (local-time:parse-timestring string :date-time-separator #\Space))


(defun execute (db sql params)
  (apply #'dbi:execute (dbi:prepare db sql) params)
  (values))

(defun query (db sql params)
  (dbi:fetch-all (apply #'dbi:execute (dbi:prepare db sql) params)))


;;;

(defclass album (listed)
  ((album-id :col-type (:varchar 256))
   (name :col-type (:varchar 256))
   (updated-at :col-type :timestamp))
  (:metaclass mito:dao-table-class)
  (:record-timestamps nil))

(defclass album-thumbnail (listed)
  ((album-id :col-type (:varchar 256))
   (thumbnail-id :col-type (:varchar 256)))
  (:metaclass mito:dao-table-class))

(defclass album-picture (listed)
  ((album-id :col-type (:varchar 256))
   (picture-id :col-type (:varchar 256)))
  (:metaclass mito:dao-table-class))

(defclass image (listed)
  ((image-id :col-type (:varchar 256))
   (path :col-type (:varchar 256)))
  (:metaclass mito:dao-table-class))

(defclass access-log-record (listed)
  ((resource-id :col-type (:varchar 256))
   (resource-type :col-type (:varchar 256))
   (accessed-at :col-type :timestamp))
  (:metaclass mito:dao-table-class)
  (:record-timestamps nil))

(defclass tag (listed)
  ((name :col-type (:varchar 256)))
  (:metaclass mito:dao-table-class))

(defclass tag-content (listed)
  ((tag-id :col-type (:varchar 256))
   (content-id :col-type (:varchar 256))
   (content-type :col-type (:varchar 256)))
  (:metaclass mito:dao-table-class))
