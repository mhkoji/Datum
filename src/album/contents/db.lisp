(defpackage :datum.album.contents.db
  (:use :cl)
  (:shadow :delete)
  (:export :select
           :insert
           :delete))
(in-package :datum.album.contents.db)

(defgeneric select (db album-id))

(defgeneric insert (db album-id content-ids))

(defgeneric delete (db album-id))
