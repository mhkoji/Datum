(defpackage :datum.web
  (:use :cl)
  (:export :start))
(in-package :datum.web)

(defvar *handler* nil)

(defun start (&key (conf (datum.app:load-configure))
                   (port 18888)
                   (document-root (namestring *default-pathname-defaults*))
                   (use-thread t))
  (when *handler*
    (clack:stop *handler*))
  (let ((app (make-instance 'ningle:<app>)))
    (datum.web.route.api:route-api app conf)
    (datum.web.route.asset:route-resources app document-root)
    (datum.web.route.asset:route-html app)
    (setq *handler* (clack:clackup app
                                   :use-thread use-thread
                                   :port port))))
