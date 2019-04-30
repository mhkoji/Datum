(defpackage :datum.app.cli.frequently-accessed
  (:use :cl)
  (:export :album-covers)
  (:import-from :datum.container
                :load-configure
                :with-container))
(in-package :datum.app.cli.frequently-accessed)

(defun album-covers (conf)
  (with-container (container conf)
    (mapcar #'datum.album:album-cover
            (mapcar #'car (datum.access-log:get-resources-and-counts
                           container
                           :album)))))
