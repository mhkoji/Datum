(defpackage :datum.db.mito.tag
  (:use :cl :datum.tag.db)
  (:export :tag
           :tag-content)
  (:import-from :dbi.driver
                :<dbi-connection>))
(in-package :datum.db.mito.tag)

(defclass tag (datum.db.mito:listed)
  ((name :col-type (:varchar 256)
         :accessor tag-name))
  (:metaclass mito:dao-table-class))

(defun dao->tag-row (obj)
  (make-tag-row :tag-id (mito.dao.mixin:object-id obj)
                :name (tag-name obj)))

(defmethod select-tag-rows ((db <dbi-connection>) offset count)
  (mapcar #'dao->tag-row
          (mito:select-dao 'tag
            (sxql:limit offset count))))

(defmethod select-tag-rows-in ((db <dbi-connection>) (tag-ids list))
  (mapcar #'dao->tag-row
          (mito:select-dao 'tag
            (sxql:where (:in :id tag-ids)))))

(defmethod insert-tag-row ((db <dbi-connection>) name)
  (dao->tag-row (mito:create-dao 'tag :name name)))

(defmethod delete-tag-rows ((db <dbi-connection>) (tag-ids list))
  (dolist (tag-id tag-ids)
    (mito:delete-by-values 'tag :id tag-id)))


(defclass tag-content (datum.db.mito:listed)
  ((tag-id :col-type (:varchar 256)
           :accessor tag-content-tag-id)
   (content-id :col-type (:varchar 256)
               :accessor tag-content-content-id)
   (content-type :col-type (:varchar 256)
                 :accessor tag-content-content-type))
  (:metaclass mito:dao-table-class))

(defun dao->tag-content-row (obj)
  (make-tag-content-row
   :tag-id (tag-content-tag-id obj)
   :content-id (datum.id:from-string (tag-content-content-id obj))
   :content-type (string-upcase (tag-content-content-type obj))))

(defmethod select-tag-rows-by-content ((db <dbi-connection>) content-id)
  (mapcar #'dao->tag-row
          (mito:select-dao 'tag
            (sxql:inner-join :tag-content
             :on (:= :tag.id :tag-content.tag-id))
            (sxql:where
             (:= :tag-content.content-id
                 (datum.id:to-string content-id))))))

(defmethod select-tag-content-rows ((db <dbi-connection>) tag-id)
  (mapcar #'dao->tag-content-row
          (mito:select-dao 'tag-content
            (sxql:where (:= :tag-id tag-id)))))

(defmethod insert-tag-content-rows ((db <dbi-connection>) (rows list))
  (dolist (row rows)
    (mito:create-dao 'tag-content
                     :tag-id (tag-content-row-tag-id row)
                     :content-id (datum.id:to-string
                                  (tag-content-row-content-id row))
                     :content-type (tag-content-row-content-type row))))

(defmethod delete-tag-content-rows-by-tags ((db <dbi-connection>)
                                            (tag-ids list))
  (dolist (tag-id tag-ids)
    (mito:delete-by-values 'tag-content :tag-id tag-id)))

(defmethod delete-tag-content-rows-by-contents ((db <dbi-connection>)
                                                (content-ids list))
  (dolist (content-id content-ids)
    (mito:delete-by-values 'tag-content
                           :content-id (datum.id:to-string content-id))))
