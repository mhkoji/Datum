(defpackage :datum.app
  (:use :cl)
  (:export :initialize)
  (:import-from :datum.container
                :load-configure
                :with-container))
(in-package :datum.app)

(defun initialize (&key (conf (load-configure)))
  (with-container (c conf)
    (datum.db:initialize (datum.container:container-db c))))
