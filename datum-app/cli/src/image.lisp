(defpackage :datum.app.cli.image
  (:use :cl)
  (:export :path)
  (:import-from :datum.container
                :with-container))
(in-package :datum.app.cli.image)

(defun path (conf id)
  (with-container (container conf)
    (let ((image (car (datum.image:load-images-by-ids
                       container
                       (list id)))))
      (datum.image:image-path image))))
