(defpackage :datum.access-log
  (:use :cl)
  (:export :container-db
           :container-resource-loader
           :load-resources-by-ids

           :resource-type
           :resource-id

           :add-record
           :load-resources-sort-by-access-count)
  (:import-from :datum.access-log.db
                :resource-type
                :resource-id))
(in-package :datum.access-log)

(defgeneric container-db (c))
(defgeneric container-resource-loader (c))

(defgeneric load-resources-by-ids (resource-loader type ids))


(defun timestamp ()
  (local-time:universal-to-timestamp (get-universal-time)))

(defun add-record (container resource)
  (let ((db (container-db container))
        (timestamp (timestamp)))
    (datum.access-log.db:insert db resource timestamp)))

(defun load-resources-sort-by-access-count (container resource-type)
  (let ((resource-ids (datum.access-log.db:select-ids-sort-by-access-count
                       (container-db container)
                       resource-type)))
    (load-resources-by-ids (container-resource-loader container)
                           resource-type
                           resource-ids)))
