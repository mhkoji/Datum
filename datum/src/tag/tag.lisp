(defpackage :datum.tag
  (:use :cl)
  (:export :container-db
           :container-content-loader
           :load-contents

           :tag
           :tag-id
           :tag-name
           :tag-contents

           :load-tags-by-ids
           :load-tags-by-range
           :save-tag
           :delete-tag

           :content-id
           :content-type
           :content-tags
           :content-set-tags))
(in-package :datum.tag)

(defgeneric container-db (c))
(defgeneric container-content-loader (c))


(defstruct tag
  id
  name)

(defgeneric content-id (content))

(defgeneric content-type (content))

(defgeneric load-contents (loader type content-ids))


(defun save-tag (container name)
  (let ((db (container-db container)))
    (let ((tag-row (datum.tag.db:insert-tag-row db name)))
      (make-tag :id (datum.tag.db:tag-row-tag-id tag-row)
                :name (datum.tag.db:tag-row-name tag-row)))))

(defun delete-tag (container tag-id)
  (let ((tag-ids (list tag-id))
        (db (container-db container)))
    (datum.tag.db:delete-tag-content-rows-by-tags db tag-ids)
    (datum.tag.db:delete-tag-rows db tag-ids))
  (values))

(defun row->tag (row)
  (make-tag :id (datum.tag.db:tag-row-tag-id row)
            :name (datum.tag.db:tag-row-name row)))

(defun load-tags-by-range (container offset count)
  (mapcar #'row->tag
          (datum.tag.db:select-tag-rows
           (container-db container)
           offset
           count)))

(defun load-tags-by-ids (container tag-ids)
  (mapcar #'row->tag
          (datum.tag.db:select-tag-rows-in
           (container-db container)
           tag-ids)))


(defun make-id-hash-table ()
  (make-hash-table :test #'equal))

(defun id-gethash (id hash)
  (gethash (datum.id:to-string id) hash))

(defun (setf id-gethash) (val id hash)
  (setf (gethash (datum.id:to-string id) hash) val))

(defun tag-contents (container tag)
  (let ((content-rows (datum.tag.db:select-tag-content-rows
                       (container-db container)
                       (tag-id tag)))
        (content-id->content (make-id-hash-table)))
    (let ((type->content-ids (make-hash-table)))
      (dolist (row content-rows)
        (let ((type (alexandria:make-keyword
                     (datum.tag.db:tag-content-row-content-type row)))
              (content-id (datum.tag.db:tag-content-row-content-id row)))
          (push content-id (gethash type type->content-ids))))
      (loop for type being the hash-keys of type->content-ids
            for contents = (load-contents (container-content-loader container)
                                          type
                                          (gethash type type->content-ids))
            do (dolist (content contents)
                 (setf (id-gethash (content-id content) content-id->content)
                       content))))
    (let ((content-ids (mapcar #'datum.tag.db:tag-content-row-content-id
                               content-rows)))
      (loop for content-id in content-ids
            for content = (id-gethash content-id content-id->content)
            when content
         collect content))))

(defun content-tags (container content)
  (mapcar #'row->tag
          (datum.tag.db:select-tag-rows-by-content
           (container-db container)
           (content-id content))))

(defun content-set-tags (container content tags)
  (let ((db (container-db container)))
    (let ((content-ids (list (content-id content))))
      (datum.tag.db:delete-tag-content-rows-by-contents db content-ids))
    (let ((rows (mapcar (lambda (tag)
                          (datum.tag.db:make-tag-content-row
                           :tag-id (tag-id tag)
                           :content-id (content-id content)
                           :content-type (content-type content)))
                        tags)))
      (datum.tag.db:insert-tag-content-rows db rows)))
  (values))
