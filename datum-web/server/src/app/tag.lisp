(defpackage :datum.web.app.tag
  (:use :cl)
  (:export :tags
           :add-tag
           :delete-tag
           :album-covers)
  (:import-from :datum.app
                :with-container))
(in-package :datum.web.app.tag)

(defun tags (conf)
  (with-container (container conf)
    (datum.tag:load-tags-by-range container 0 50)))

(defun add-tag (conf name)
  (with-container (container conf)
    (datum.tag:save-tag container name)))


(defun delete-tag (conf tag-id)
  (with-container (container conf)
    (datum.tag:delete-tag container tag-id)))

(defun album-covers (conf tag-id)
  (with-container (container conf)
    (let ((tag (car (datum.tag:load-tags-by-ids container (list tag-id)))))
      (let ((albums (datum.tag:tag-contents container tag)))
        (mapcar #'datum.album:album-cover albums)))))
