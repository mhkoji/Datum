(defpackage :datum.access-log.db
  (:use :cl)
  (:export :resource-type
           :resource-id
           :insert
           :select-ids-sort-by-access-count))
(in-package :datum.access-log.db)

(defgeneric resource-type (resource))
(defgeneric resource-id (resource))

(defgeneric insert (db resource timestamp))
(defgeneric select-ids-sort-by-access-count (db resource-type))
