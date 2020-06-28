(defpackage :datum.db.mito.album
  (:use :cl :datum.album.db)
  (:export :album
           :album-thumbnail)
  (:import-from :dbi.driver
                :<dbi-connection>))
(in-package :datum.db.mito.album)

(defmacro conc-strings (&rest strings)
  `(concatenate 'string ,@strings))

(defun query (db sql params)
  (dbi:fetch-all (apply #'dbi:execute (dbi:prepare db sql) params)))

(defclass album (datum.db.mito:listed)
  ((album-id :col-type (:varchar 256)
             :accessor album-id)
   (name :col-type (:varchar 256)
         :accessor album-name)
   (updated-at :col-type :timestamp
               :accessor album-updated-at))
  (:metaclass mito:dao-table-class)
  (:record-timestamps nil))

(defmethod insert-album-rows ((db <dbi-connection>)
                              (rows list))
  (dolist (row rows)
    (mito:create-dao 'album
                     :album-id (datum.id:to-string (album-row-id row))
                     :name (album-row-name row)
                     :updated-at (album-row-updated-at row))))

(defmethod select-album-rows ((db <dbi-connection>)
                              (album-ids list))
  (when album-ids
    (let ((sql (conc-strings
                "SELECT *"
                " FROM"
                "  album"
                " WHERE"
                "  album_id in ("
                (format nil "~{~A~^,~}"
                        (loop repeat (length album-ids) collect "?"))
                ")")))
      (let ((plist-rows
             (query db sql
                    (mapcar #'datum.id:to-string album-ids))))
        (mapcar (lambda (plist)
                  (make-album-row
                   :id (datum.id:from-string (getf plist :|album_id|))
                   :name (getf plist :|name|)
                   :updated-at (local-time:parse-timestring
                                (getf plist :|updated_at|)
                                :date-time-separator #\Space)))
                plist-rows)))))

(defmethod select-album-ids ((db <dbi-connection>)
                             offset count)
  (let ((sql (conc-strings
              "SELECT album_id"
              " FROM"
              "  album"
              " ORDER BY"
              "  updated_at DESC"
              " LIMIT"
              "  ?, ?")))
    (let ((plist-rows
           (query db sql (list offset count))))
      (mapcar (lambda (plist)
                (datum.id:from-string (getf plist :|album_id|)))
              plist-rows))))

(defmethod select-album-ids-by-like ((db <dbi-connection>)
                                     (name string))
  (mapcar (alexandria:compose #'datum.id:from-string #'album-id)
          (mito:select-dao 'album
            (sxql:where (:like :name (format nil "%~A%" name)))
            (sxql:order-by (:desc :updated_at))
            (sxql:limit 50))))


(defmethod delete-album-rows ((db <dbi-connection>) (album-ids list))
  (dolist (album-id album-ids)
    (mito:delete-by-values 'album
                           :album-id (datum.id:to-string album-id))))


(defclass album-thumbnail (datum.db.mito:listed)
  ((album-id :col-type (:varchar 256)
             :accessor album-id)
   (thumbnail-id :col-type (:varchar 256)
                 :accessor album-thumbnail-id))
  (:metaclass mito:dao-table-class))

(defmethod insert-album-thumbnail-rows ((db <dbi-connection>)
                                        (rows list))
  (dolist (row rows)
    (mito:create-dao 'album-thumbnail
                     :album-id
                     (datum.id:to-string
                      (album-thumbnail-row-album-id row))
                     :thumbnail-id
                     (datum.id:to-string
                      (album-thumbnail-row-thumbnail-id row)))))

(defmethod select-album-thumbnail-rows ((db <dbi-connection>)
                                        (album-ids list))
  (let ((objects (mito:select-dao 'album-thumbnail
                   (sxql:where (:in :album-id album-ids)))))
    (mapcar (lambda (obj)
              (make-album-thumbnail-row
               :album-id
               (datum.id:from-string (album-id obj))
               :thumbnail-id
               (datum.id:from-string (album-thumbnail-id obj))))
            objects)))

(defmethod delete-album-thumbnail-rows ((db <dbi-connection>)
                                        (album-ids list))
  (dolist (album-id album-ids)
    (mito:delete-by-values 'album-thumbnail
                           :album-id (datum.id:to-string album-id))))
