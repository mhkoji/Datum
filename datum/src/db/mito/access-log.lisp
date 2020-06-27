(defpackage :datum.db.mito.access-log
  (:use :cl :datum.access-log.db)
  (:export :access-log-record)
  (:import-from :dbi.driver
                :<dbi-connection>))
(in-package :datum.db.mito.access-log)

(defclass access-log-record (datum.db.mito:listed)
  ((resource-id :col-type (:varchar 256)
                :accessor record-resource-id)
   (resource-type :col-type (:varchar 256)
                  :accessor record-resource-type)
   (accessed-at :col-type :timestamp
                :accessor record-accessed-at))
  (:metaclass mito:dao-table-class)
  (:record-timestamps nil))

(defmethod insert ((db <dbi-connection>) resource accessed-at)
  (mito:create-dao 'access-log-record
                   :resource-id   (datum.id:to-string (resource-id resource))
                   :resource-type (resource-type resource)
                   :accessed-at   accessed-at))

(defmethod count-accesses ((db <dbi-connection>) resource-type)
  (let ((sxq (sxql:select (:resource_id
                           (:as (:count :*) :count))
               (sxql:from :access_log_record)
               (sxql:where (:= :resource_type :?))
               (sxql:group-by :resource_id)
               (sxql:order-by (:desc :count)))))
    (let* ((query (dbi:prepare db (sxql:yield sxq)))
           (result (dbi:execute query (string-downcase
                                       (string resource-type)))))
      (loop for row = (dbi:fetch result)
            while row
            collect (make-access-count
                     :resource-id (datum.id:from-string
                                   (getf row :|resource_id|))
                     :count (getf row :|count|))))))
