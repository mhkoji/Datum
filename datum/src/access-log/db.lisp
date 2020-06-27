(defpackage :datum.access-log.db
  (:use :cl)
  (:export :resource-type
           :resource-id
           :insert
           :make-access-count
           :access-count-resource-id
           :access-count-count
           :count-accesses))
(in-package :datum.access-log.db)

(defgeneric resource-type (resource))
(defgeneric resource-id (resource))


(defstruct access-count resource-id count)

(defgeneric insert (db resource accessed-at))
(defgeneric count-accesses (db resource-type))
