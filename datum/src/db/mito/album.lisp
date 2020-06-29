(in-package :datum.db.mito)

(defmethod datum.album.db:insert-album-rows ((db <dbi-connection>)
                                             (rows list))
  (when rows
    (let ((sql (conc-strings
                "INSERT INTO album"
                " (album_id, name, updated_at)"
                " VALUES"
                (format nil "~{~A~^,~}"
                        (loop repeat (length rows) collect "(?,?,?)")))))
      (execute db
               sql
               (alexandria:mappend
                (lambda (row)
                  (list (datum.id:to-string
                         (datum.album.db:album-row-id row))
                        (datum.album.db:album-row-name row)
                        (format-datetime
                         (datum.album.db:album-row-updated-at row))))
                rows)))))

(defmethod datum.album.db:select-album-rows ((db <dbi-connection>)
                                             (album-ids list))
  (when album-ids
    (let ((sql (conc-strings
                "SELECT *"
                " FROM"
                "  album"
                " WHERE"
                "  album_id in"
                " ("
                (format nil "~{~A~^,~}"
                        (loop repeat (length album-ids) collect "?"))
                " )")))
      (let ((plist-rows
             (query db sql (mapcar #'datum.id:to-string album-ids))))
        (mapcar (lambda (plist)
                  (datum.album.db:make-album-row
                   :id (datum.id:from-string (getf plist :|album_id|))
                   :name (getf plist :|name|)
                   :updated-at (parse-datetime
                                (getf plist :|updated_at|))))
                plist-rows)))))

(labels ((get-album-ids (plist-rows)
           (mapcar (lambda (plist)
                     (datum.id:from-string (getf plist :|album_id|)))
                   plist-rows)))
  (defmethod datum.album.db:select-album-ids ((db <dbi-connection>)
                                              offset count)
    (let ((sql (conc-strings
                "SELECT album_id"
                " FROM"
                "  album"
                " ORDER BY"
                "  updated_at DESC"
                " LIMIT"
                "  ?, ?")))
      (get-album-ids
       (query db sql (list offset count)))))

  (defmethod datum.album.db:select-album-ids-by-like ((db <dbi-connection>)
                                                      (name string))
    (let ((sql (conc-strings
                "SELECT album_id"
                " FROM"
                "  album"
                " WHERE"
                "  name like ?"
                " ORDER BY"
                "  updated_at DESC"
                " LIMIT"
                "  50")))
      (get-album-ids
       (query db sql (list (format nil "%~A%" name)))))))

(defmethod datum.album.db:delete-album-rows ((db <dbi-connection>)
                                             (album-ids list))
  (when album-ids
    (let ((sql (conc-strings
                "DELETE"
                " FROM"
                "  album"
                " WHERE"
                "  album_id in"
                " ("
                (format nil "~{~A~^,~}"
                        (loop repeat (length album-ids) collect "?"))
                " )")))
      (execute db sql (mapcar #'datum.id:to-string album-ids)))))

(defmethod datum.album.db:insert-album-thumbnail-rows ((db <dbi-connection>)
                                                       (rows list))
  (when rows
    (let ((sql (conc-strings
                "INSERT INTO album_thumbnail"
                " (album_id, thumbnail_id)"
                " VALUES"
                (format nil "~{~A~^,~}"
                        (loop repeat (length rows) collect "(?,?)"))))
          (params
           (alexandria:mappend
            (lambda (row)
              (list (datum.id:to-string
                     (datum.album.db:album-thumbnail-row-album-id row))
                    (datum.id:to-string
                     (datum.album.db:album-thumbnail-row-thumbnail-id row))))
            rows)))
      (execute db sql params))))

(defmethod datum.album.db:select-album-thumbnail-rows ((db <dbi-connection>)
                                                       (album-ids list))
  (when album-ids
    (let ((sql (conc-strings
                "SELECT *"
                " FROM"
                "  album_thumbnail"
                " WHERE"
                "  album_id in"
                " ("
                (format nil "~{~A~^,~}"
                        (loop repeat (length album-ids) collect "?"))
                " )")))
      (let ((plist-rows
             (query db sql (mapcar #'datum.id:to-string album-ids))))
        (mapcar (lambda (plist)
                  (datum.album.db:make-album-thumbnail-row
                   :album-id
                   (datum.id:from-string (getf plist :|album_id|))
                   :thumbnail-id
                   (datum.id:from-string (getf plist :|thumbnail_id|))))
                plist-rows)))))

(defmethod datum.album.db:delete-album-thumbnail-rows ((db <dbi-connection>)
                                                       (album-ids list))
  (when album-ids
    (let ((sql (conc-strings
                "DELETE"
                " FROM"
                "  album_thumbnail"
                " WHERE"
                "  album_id in"
                " ("
                (format nil "~{~A~^,~}"
                        (loop repeat (length album-ids) collect "?"))
                " )")))
      (execute db sql (mapcar #'datum.id:to-string album-ids)))))

(defmethod datum.album.pictures.db:insert-pictures ((db <dbi-connection>)
                                                    (album-id t)
                                                    (picture-ids list))
  (when picture-ids
    (let ((sql (conc-strings
                "INSERT INTO album_picture"
                " (album_id, picture_id)"
                " VALUES"
                (format nil "~{~A~^,~}"
                        (loop repeat (length picture-ids) collect "(?,?)"))))
          (params
           (alexandria:mappend
            (lambda (picture-id)
              (list (datum.id:to-string album-id)
                    (datum.id:to-string picture-id)))
            picture-ids)))
      (execute db sql params))))

(defmethod datum.album.pictures.db:select-pictures ((db <dbi-connection>)
                                                    (album-ids list))
  (when album-ids
    (let ((sql (conc-strings
                "SELECT *"
                " FROM"
                "  album_picture"
                " WHERE"
                "  album_id in"
                " ("
                (format nil "~{~A~^,~}"
                        (loop repeat (length album-ids) collect "?"))
                " )")))
      (let ((plist-rows
             (query db sql (mapcar #'datum.id:to-string album-ids))))
        (mapcar (lambda (plist)
                  (datum.id:from-string (getf plist :|picture_id|)))
                plist-rows)))))

(defmethod datum.album.pictures.db:delete-pictures ((db <dbi-connection>)
                                                    (album-ids list))
  (when album-ids
    (let ((sql (conc-strings
                "DELETE"
                " FROM"
                "  album_picture"
                " WHERE"
                "  album_id in"
                " ("
                (format nil "~{~A~^,~}"
                        (loop repeat (length album-ids) collect "?"))
                " )")))
      (execute db sql (mapcar #'datum.id:to-string album-ids)))))
