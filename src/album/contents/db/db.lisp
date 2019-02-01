(defpackage :datum.album.contents.db
  (:use :cl)
  (:export :insert-contents
           :select-contents
           :delete-contents))
(in-package :datum.album.contents.db)

(defgeneric insert-contents (db album-id content-ids))

(defgeneric select-contents (db album-ids))

(defgeneric delete-contents (db album-ids))
