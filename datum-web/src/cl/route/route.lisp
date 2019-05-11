(defpackage :datum.web.route
  (:use :cl)
  (:export :as-json
           :as-file
           :route))
(in-package :datum.web.route)

(defun as-json (obj &optional (success t))
  (let ((resp ningle:*response*))
    (alexandria:appendf (lack.response:response-headers resp)
                        (list :content-type "application/json")))
  (datum.web.json:make-result obj (if success :t :f)))

(defun as-file (path)
  (funcall (lack.app.file:make-app :file path :root "/") nil))


(defun query (params q)
  (cdr (assoc q params :test #'string=)))

(defun param (params key)
  (cdr (assoc key params)))

(defmacro route (app path &key (method :get)
                               args
                               perform
                               (output '#'as-json))
  `(setf (ningle:route ,app ,path :method ,method)
         (lambda (params)
           (declare (ignorable params))
           (handler-case
               (funcall ,output
                        (let ,(mapcar
                               (lambda (arg)
                                 (destructuring-bind (var (type key)) arg
                                   (ecase type
                                     (:query `(,var (query params ,key)))
                                     (:param `(,var (param params ',key))))))
                               args)
                          ,perform))
             (error (c)
               (declare (ignore c))
               (as-json nil nil))))))
