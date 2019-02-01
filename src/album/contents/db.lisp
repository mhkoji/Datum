(defpackage :datum.album.contents.db
  (:use :cl)
  (:export :save-contents
           :load-contents
           :delete-contents))
(in-package :datum.album.contents.db)

(defgeneric save-contents (db album-id content-ids))

(defgeneric load-contents (db album-id))

(defgeneric delete-contents (db album-id))
