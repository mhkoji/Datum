(defpackage :datum.tag
  (:use :cl)
  (:export :tag
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
    (datum.tag.db:delete-tag-content-rows-by-tags db tag-ids)
    (datum.tag.db:delete-tag-rows db tag-ids))
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
            for contents = (load-contents loader
                                          type
                                          (gethash type type->content-ids))
            do (dolist (content contents)
                 (setf (gethash (content-id content) content-id->content)
                       content))))
    (let ((content-ids (mapcar #'datum.tag.db:tag-content-row-content-id
                               content-rows)))
      (loop for content-id in content-ids
            for content = (gethash content-id content-id->content)
            when content
         collect content))))


(defun row->tag (row)
  (make-tag :id (datum.tag.db:tag-row-tag-id row)
            :name (datum.tag.db:tag-row-name row)))

(defun load-tags-by-range (db offset count)
  (mapcar #'row->tag
          (datum.tag.db:select-tag-rows db offset count)))

(defun load-tags-by-ids (db tag-ids)
  (mapcar #'row->tag
          (datum.tag.db:select-tag-rows-in db tag-ids)))



(defun content-tags (content db)
  (mapcar #'row->tag
          (datum.tag.db:select-tag-rows-by-content
           db
           (content-id content))))

(defun content-set-tags (content tags db)
  (let ((content-ids (list (content-id content))))
    (datum.tag.db:delete-tag-content-rows-by-contents db content-ids))
  (let ((rows (mapcar (lambda (tag)
                        (datum.tag.db:make-tag-content-row
                         :tag-id (tag-id tag)
                         :content-id (content-id content)
                         :content-type (content-type content)))
                      tags)))
    (datum.tag.db:insert-tag-content-rows db rows))
  (values))
