(defpackage :datum.access-log
  (:use :cl)
  (:export :container-db
           :container-resource-loader
           :load-resources-by-ids

           :resource-type
           :resource-id

           :add-record
           :get-resources-and-counts)
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
        (accessed-at (timestamp)))
    (datum.access-log.db:insert db resource accessed-at)))

(defun get-resources-and-counts (container resource-type)
  (let ((access-counts (datum.access-log.db:count-accesses
                        (container-db container)
                        resource-type)))
    (let ((resources
           (load-resources-by-ids
            (container-resource-loader container)
            resource-type
            (mapcar #'datum.access-log.db:access-count-resource-id
                    access-counts))))
      (mapcar (lambda (resource access-count)
                (cons resource
                      (datum.access-log.db:access-count-count
                       access-count)))
              resources access-counts))))
