(defpackage :datum.app.web
  (:use :cl)
  (:export :start)
  (:import-from :datum.container
                :load-configure))
(in-package :datum.app.web)

(defvar *handler* nil)

(defun start (&key (port 18888)
                   (conf (load-configure)))
  (when *handler*
    (clack:stop *handler*))
  (let ((app (make-instance 'ningle:<app>)))
    (datum.app.web.route.api:bind-album app conf)
    (datum.app.web.route.api:bind-image app conf)
    (datum.app.web.route.api:bind-tag app conf)
    (datum.app.web.route.asset:bind-resources
     app (namestring *default-pathname-defaults*))
    (datum.app.web.route.asset:bind-html app)
    (setq *handler* (clack:clackup app :port port))))
