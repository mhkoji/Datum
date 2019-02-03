(defpackage :datum.app.web.route
  (:use :cl)
  (:export :as-json
           :as-file
           :bind-route!))
(in-package :datum.app.web.route)

(defun as-json (obj &optional (success t))
  (let ((resp ningle:*response*))
    (alexandria:appendf (lack.response:response-headers resp)
                        (list :content-type "application/json")))
  (datum.app.web.json:make-result obj (if success :t :f)))

(defun as-file (path)
  (funcall (lack.app.file:make-app :file path :root "/") nil))


(defun query (params q)
  (cdr (assoc q params :test #'string=)))

(defun param (params key)
  (cdr (assoc key params)))

(defmacro bind-route! (app path args callback
                       &key (method :get) (out '#'as-json))
  `(setf (ningle:route ,app ,path :method ,method)
         (lambda (params)
           (declare (ignorable params))
           (handler-case
               (funcall ,out
                        (,callback
                         ,@(mapcar (lambda (arg)
                                     (destructuring-bind (type key) arg
                                       (ecase type
                                         (:query `(query params ,key))
                                         (:param `(param params ,key)))))
                                   args)))
             (error (c)
               (declare (ignore c))
               (as-json nil nil))))))
