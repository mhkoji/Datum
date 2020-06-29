(in-package :datum.db.mito)

(defmethod datum.access-log.db:insert ((db <dbi-connection>)
                                       resource
                                       accessed-at)
  (execute db
           (conc-strings
            "INSERT INTO access_log_record"
            " (resource_id, resource_type, accessed_at)"
            " VALUES"
            " (?,?,?)")
           (list (datum.id:to-string
                  (datum.access-log.db:resource-id resource))
                 (string-downcase
                  (string (datum.access-log.db:resource-type resource)))
                 (format-datetime accessed-at))))

(defmethod datum.access-log.db:count-accesses ((db <dbi-connection>)
                                               resource-type)
  (let ((sql
         (conc-strings
          "SELECT resource_id, count(*) as count"
          " FROM"
          "  access_log_record"
          " WHERE"
          "  resource_type = ?"
          " GROUP BY"
          "  resource_id"
          " ORDER BY"
          "  count DESC"
          " LIMIT 50")))
    (let ((plist-rows
           (query db sql (list (string-downcase
                                (string resource-type))))))
      (loop for row in plist-rows
            collect (datum.access-log.db:make-access-count
                     :resource-id
                     (datum.id:from-string (getf row :|resource_id|))
                     :count
                     (getf row :|count|))))))
