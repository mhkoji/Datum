(in-package :datum.db.mito)

(labels ((to-tag-row (plist)
           (datum.tag.db:make-tag-row
            :tag-id
            (getf plist :|id|)
            :name
            (getf plist :|name|))))
  (defmethod datum.tag.db:select-tag-rows ((db <dbi-connection>)
                                           offset
                                           count)
    (let ((sql (conc-strings
                "SELECT *"
                " FROM"
                "  tag"
                " LIMIT"
                "  ?,?")))
      (mapcar #'to-tag-row
              (query db sql (list offset count)))))

  (defmethod datum.tag.db:select-tag-rows-in ((db <dbi-connection>)
                                              (tag-ids list))
    (when tag-ids
      (let ((sql (conc-strings
                  "SELECT *"
                  " FROM"
                  "  tag"
                  " WHERE"
                  "  id in"
                  " ("
                  (format nil "~{~A~^,~}"
                          (loop repeat (length tag-ids) collect "?"))
                  " )")))
        (mapcar #'to-tag-row
                (query db sql tag-ids)))))


  (defmethod datum.tag.db:insert-tag-row ((db <dbi-connection>)
                                          name)
    (execute db
             (conc-strings
              "INSERT INTO tag"
              " (name)"
              "  VALUES"
              " (?)")
             (list name))
    (let ((id (mito.db:last-insert-id db "tag" "id")))
      (to-tag-row (list :|id| id :|name| name))))

  (defmethod datum.tag.db:select-tag-rows-by-content ((db <dbi-connection>)
                                                      content-id)
    (let ((sql (conc-strings
                "SELECT *"
                " FROM"
                "  tag"
                " INNER JOIN"
                "  tag_content"
                " ON"
                "  tag.id = tag_content.tag_id"
                " WHERE"
                "  tag_content.content_id = ?")))
      (mapcar #'to-tag-row
              (query db sql (list (datum.id:to-string content-id)))))))

(defmethod datum.tag.db:delete-tag-rows ((db <dbi-connection>)
                                         (tag-ids list))
  (when tag-ids
    (let ((sql (conc-strings
                "DELETE"
                " FROM"
                "  tag"
                " WHERE"
                "  id in"
                " ("
                (format nil "~{~A~^,~}"
                        (loop repeat (length tag-ids) collect "?"))
                " )")))
      (execute db sql tag-ids))))


(labels ((to-tag-content-rows (plist-rows)
           (mapcar (lambda (plist)
                     (datum.tag.db:make-tag-content-row
                      :tag-id
                      (getf plist :|tag_id|)
                      :content-id
                      (datum.id:from-string
                       (getf plist :|content_id|))
                      :content-type
                      (string-upcase (getf plist :|content_type|))))
                   plist-rows)))
  (defmethod datum.tag.db:select-tag-content-rows ((db <dbi-connection>)
                                                   tag-id)
    (to-tag-content-rows
     (query db
            "SELECT * FROM tag_content WHERE tag_id = ?"
            (list tag-id)))))

(defmethod datum.tag.db:insert-tag-content-rows ((db <dbi-connection>)
                                                 (rows list))
  (when rows
    (execute db
             (conc-strings
              "INSERT INTO tag_content (tag_id, content_id, content_type)"
              " VALUES"
              (format nil "~{~A~^,~}"
                      (loop repeat (length rows) collect "(?,?,?)")))
             (alexandria:mappend
              (lambda (row)
                (list (datum.tag.db:tag-content-row-tag-id row)
                      (datum.id:to-string
                       (datum.tag.db:tag-content-row-content-id row))
                      (string-downcase
                       (string
                        (datum.tag.db:tag-content-row-content-type row)))))
              rows))))

(defmethod datum.tag.db:delete-tag-content-rows-by-tags ((db <dbi-connection>)
                                                         (tag-ids list))
  (when tag-ids
    (execute db
             (conc-strings
              "DELETE FROM tag_content WHERE tag_id in"
              " ("
              (format nil "~{~A~^,~}"
                      (loop repeat (length tag-ids) collect "?"))
              " )")
             tag-ids)))

(defmethod datum.tag.db:delete-tag-content-rows-by-contents
    ((db <dbi-connection>)
     (content-ids list))
  (when content-ids
    (execute db
             (conc-strings
              "DELETE FROM tag_content WHERE content_id in"
              " ("
              (format nil "~{~A~^,~}"
                      (loop repeat (length content-ids) collect "?"))
              " )")
             (mapcar #'datum.id:to-string content-ids))))
