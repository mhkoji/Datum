(defpackage :datum.web.app.frequently-accessed
  (:use :cl)
  (:export :album-covers)
  (:import-from :datum.app
                :with-container))
(in-package :datum.web.app.frequently-accessed)

(defun album-covers (conf)
  (with-container (container conf)
    (mapcar #'datum.album:album-cover
            (mapcar #'car (datum.access-log:get-resources-and-counts
                           container
                           :album)))))
