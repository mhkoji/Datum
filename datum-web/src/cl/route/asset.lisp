(defpackage :datum.web.route.asset
  (:use :cl)
  (:export :bind-html
           :bind-resources))
(in-package :datum.web.route.asset)

(defun bind-html (app)
  (setf (ningle:route app "/.*" :method :get :regexp t)
        (lambda (params)
          (declare (ignore params))
          (datum.web.html:main "/resources/compiled/cljs/bundle.js")))
  app)


(defun bind-resources (app root)
  (setf (ningle:route app "/resources/*.*" :method :get)
        (lambda (params)
          (destructuring-bind (path type) (cdr (assoc :splat params))
            (funcall (lack.app.file:make-app
                      :file (format nil "~A/resources/~A.~A"
                                    root path type)
                      :root "/")
                     nil))))
  app)
