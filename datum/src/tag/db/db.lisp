(defpackage :datum.tag.db
  (:use :cl)
  (:export :select-tag-rows
           :select-tag-rows-in
           :insert-tag-row
           :delete-tag-rows
           :make-tag-row
           :tag-row-tag-id
           :tag-row-name

           :select-tag-rows-by-content
           :select-tag-content-rows
           :insert-tag-content-rows
           :delete-tag-content-rows-by-tags
           :delete-tag-content-rows-by-contents
           :make-tag-content-row
           :tag-content-row-tag-id
           :tag-content-row-content-id
           :tag-content-row-content-type))
(in-package :datum.tag.db)

(defstruct tag-row
  tag-id
  name)

(defgeneric select-tag-rows (db offset count))
(defgeneric select-tag-rows-in (db tag-ids))
(defgeneric insert-tag-row (db name))
(defgeneric delete-tag-rows (db tag-ids))



(defstruct tag-content-row
  tag-id
  content-id
  content-type)

(defgeneric select-tag-rows-by-content (db content-id))
(defgeneric select-tag-content-rows (db tag-id))
(defgeneric insert-tag-content-rows (db rows))
(defgeneric delete-tag-content-rows-by-tags (db tag-ids))
(defgeneric delete-tag-content-rows-by-contents (db content-ids))
