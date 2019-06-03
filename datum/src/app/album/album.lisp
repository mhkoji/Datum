(defpackage :datum.app.album
  (:use :cl)
  (:shadow :search)
  (:export :add-albums
           :covers
           :search
           :overview
           :tags
           :set-tags)
  (:import-from :datum.app
                :with-container))
(in-package :datum.app.album)

(defun covers (conf offset count)
  (with-container (container conf)
    (let ((albums (datum.album:load-albums-by-range
                   container
                   (or offset 0)
                   (or count 500))))
      (mapcar #'datum.album:album-cover albums))))

(defun search (conf keyword)
  (with-container (container conf)
    (let ((albums (datum.album:search-albums
                   container keyword)))
      (mapcar #'datum.album:album-cover albums))))

(labels ((load-album-by-id (container album-id)
           (car (datum.album:load-albums-by-ids
                 container
                 (list album-id)))))
  (defun overview (conf album-id)
    (with-container (container conf)
      (let ((album (load-album-by-id container album-id)))
        (datum.access-log:add-record container album)
        (datum.album:album-overview album container))))

  (defun tags (conf album-id)
    (with-container (container conf)
      (let ((album (load-album-by-id container album-id)))
        (datum.tag:content-tags container album))))

  (defun set-tags (conf album-id tag-ids)
    (with-container (container conf)
      (let ((album (load-album-by-id container album-id))
            (tags (datum.tag:load-tags-by-ids container tag-ids)))
        (datum.tag:content-set-tags container album tags)))
    (values)))
