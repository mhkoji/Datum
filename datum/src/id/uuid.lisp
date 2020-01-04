(defpackage :datum.id
  (:use :cl)
  (:export :gen
           :to-string
           :from-string
           :to-string-short
           :from-string-short))
(in-package :datum.id)

(defclass base64-uuid (uuid:uuid) ())

(defun gen (string)
  "Generate UUID for identifying various domain objects."
  (let ((uuid (uuid:make-v5-uuid uuid:+namespace-dns+ string)))
    (change-class uuid 'base64-uuid)))


(defun to-string (id)
  "Convert id into a string for storing to a DB and such"
  (format nil "~A" id))

(defun from-string (string)
  (let ((uuid (uuid:make-uuid-from-string string)))
    (change-class uuid 'base64-uuid)))


(defun to-string-short (id)
  "Convert id into a short string used for URL and such."
  (let ((octets (uuid:uuid-to-byte-array id)))
    (let ((base64 (cl-base64:usb8-array-to-base64-string octets :uri t)))
      (subseq base64 0 22))))

(defun from-string-short (string)
  (let ((octets (cl-base64:base64-string-to-usb8-array
                 (format nil "~A.." string) :uri t)))
    (let ((uuid (uuid:byte-array-to-uuid octets)))
      (change-class uuid 'base64-uuid))))
