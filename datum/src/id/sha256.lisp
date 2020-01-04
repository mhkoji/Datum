(defpackage :datum.id
  (:use :cl)
  (:export :gen
           :to-string
           :from-string
           :to-string-short
           :from-string-short))
(in-package :datum.id)

(defun sha256 (string)
  (ironclad:byte-array-to-hex-string
   (let ((octets (babel:string-to-octets string :encoding :utf-8)))
     (ironclad:digest-sequence 'ironclad:sha256 octets))))


(defclass sha256-id ()
  ((string :initarg :string
           :reader sha256-id-string)))

(defun gen (string)
  (make-instance 'sha256-id :string (sha256 string)))


(defun to-string (sha256-id)
  (sha256-id-string sha256-id))

(defun to-string-short (sha256-id)
  (to-string sha256-id))


(defun from-string (string)
  (make-instance 'sha256-id :string string))

(defun from-string-short (string)
  (from-string string))
