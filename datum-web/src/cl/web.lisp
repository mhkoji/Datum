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
    (datum.web.route.api:bind-all app conf)
    (datum.web.route.asset:bind-resources
     app (namestring *default-pathname-defaults*))
    (datum.web.route.asset:bind-html app)
    (setq *handler* (clack:clackup app
                                   :use-thread use-thread
                                   :port port))))
