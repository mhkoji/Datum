(defpackage :datum.app.image
  (:use :cl)
  (:export :path)
  (:import-from :datum.app
                :with-container))
(in-package :datum.app.image)

(defun path (conf id)
  (with-container (container conf)
    (let ((image (car (datum.image:load-images-by-ids
                       container
                       (list id)))))
      (datum.image:image-path image))))
