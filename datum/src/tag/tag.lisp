(defpackage :datum.tag
  (:use :cl)
  (:export :tag
           :tag-id
           :tag-name

           :save-tag
           :delete-tag

           :content-id
           :content-type))
(in-package :datum.tag)

(defstruct tag
  id
  name)

(defgeneric content-id (content))

(defgeneric content-type (content))

(defgeneric load-contents (loader type content-ids))



(defun save-tag (db name)
  (let ((tag-row (datum.tag.db:insert-tag-row db name)))
    (make-tag :id (datum.tag.db:tag-row-tag-id tag-row)
              :name (datum.tag.db:tag-row-name tag-row))))


(defun delete-tag (db tag-id)
  (let ((tag-ids (list tag-id)))
    (datum.tag.db:delete-tag-content-rows db tag-ids)
    (datum.tag.db:delete-tag-rows db tag-ids))
  (values))


(defun attach-tag (db tag content)
  (let ((row (datum.tag.db:make-tag-content-row
              :tag-id (tag-id tag)
              :content-id (content-id content)
              :content-type (content-type content))))
    (datum.tag.db:insert-tag-content-rows db (list row)))
  (values))

(defun detach-tag (db tag content)
  (datum.tag.db:delete-tag-content-rows-only
   db
   (tag-id tag)
   (list (content-id content)))
  (values))


(defun content-tags (content db)
  (datum.tag.db:select-tag-rows-by-content db (content-id content)))

(defun set-content-tags (db content tags)
  (dolist (tag (content-tags content db))
    (detach-tag db tag content))
  (dolist (tag tags)
    (attach-tag db tag content))
  (values))

(defun tag-contents (tag db loader)
  (let ((content-rows (datum.tag.db:select-tag-content-rows
                       db
                       (tag-id tag)))
        (content-id->content (make-hash-table :test #'equal)))
    (let ((type->content-ids (make-hash-table)))
      (dolist (row content-rows)
        (let ((type (alexandria:make-keyword
                     (datum.tag.db:tag-content-row-content-type row)))
              (content-id (datum.tag.db:tag-content-row-content-id row)))
          (push content-id (gethash type type->content-ids))))
      (loop for type being the hash-keys of type->content-ids
            for content-ids = (gethash type type->content-ids)
            for contents = (load-contents loader type content-ids)
            do (dolist (content contents)
                 (setf (gethash (content-id content)
                                content-id->content)
                       content))))
    (let ((content-ids (mapcar #'datum.tag.db:tag-content-row-content-id
                               content-rows)))
      (mapcar (lambda (content-id)
                (gethash content-id content-id->content))
              content-ids))))
