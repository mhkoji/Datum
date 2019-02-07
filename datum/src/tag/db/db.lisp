(defpackage :datum.tag.db
  (:use :cl)
  (:export :select-tag-rows-by-content
           :insert-tag-row
           :delete-tag-rows
           :tag-row-tag-id
           :tag-row-name

           :select-tag-content-rows
           :insert-tag-content-rows
           :delete-tag-content-rows
           :delete-tag-content-rows-only
           :make-tag-content-row
           :tag-content-row-content-id
           :tag-content-row-content-type))
(in-package :datum.tag.db)

(defstruct tag-row
  tag-id
  name)

(defgeneric insert-tag-row (db name))
(defgeneric delete-tag-rows (db tag-ids))
(defgeneric select-tag-rows-by-content (db content-id))


(defstruct tag-content-row
  tag-id
  content-id
  content-type)

(defgeneric select-tag-content-rows (db tag-id))
(defgeneric insert-tag-content-rows (db rows))
(defgeneric delete-tag-content-rows (db tag-ids))
(defgeneric delete-tag-content-rows-only (db tag-id content-ids))
