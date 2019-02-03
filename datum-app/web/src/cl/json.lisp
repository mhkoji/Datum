(defpackage :datum.app.web.json
  (:use :cl)
  (:export :make-result))
(in-package :datum.app.web.json)

(defgeneric as-jsown (obj))

(defmethod as-jsown ((obj list))
  (mapcar #'as-jsown obj))

(defmethod as-jsown ((obj datum.image:image))
  (let ((image-id (datum.image:image-id obj)))
    (jsown:new-js
      ("image-id" image-id))))

(defmethod as-jsown ((obj datum.album:cover))
  (jsown:new-js
    ("album-id"
     (datum.album:cover-album-id obj))
    ("name"
     (datum.album:cover-name obj))
    ("thumbnail"
     (as-jsown (datum.album:cover-thumbnail obj)))))

(defmethod as-jsown ((obj datum.album:overview))
  (jsown:new-js
    ("album-id"
     (datum.album:overview-album-id obj))
    ("name"
     (datum.album:overview-name obj))
    ("pictures"
     (as-jsown (datum.album:overview-pictures obj)))))


(defun make-result (obj success)
  (jsown:to-json
   (jsown:new-js
     ("success" (if success :t :f))
     ("result"  (as-jsown obj)))))
