(defpackage :datum.web
  (:use :cl)
  (:export :start)
  (:import-from :datum.container
                :load-configure))
(in-package :datum.web)

(defvar *handler* nil)

(defun start (&key (port 18888)
                   (use-thread t)
                   (conf (load-configure)))
  (when *handler*
    (clack:stop *handler*))
  (let ((app (make-instance 'ningle:<app>)))
    (datum.web.route.api:route-api app conf)
    (datum.web.route.asset:route-resources
     app (namestring *default-pathname-defaults*))
    (datum.web.route.asset:route-html app)
    (setq *handler* (clack:clackup app
                                   :use-thread use-thread
                                   :port port))))
