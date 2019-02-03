;;; What a controller does is:
;;; 1. Receive an input
;;; 2. Prepare objects used for executing the controller task
;;; 3. Execute the task with the input and prepared objects
;;; 4. Send the output of the task in a formatted style
(defpackage :datum.app.web.route.api
  (:use :cl
        :datum.app.web.route
        :datum.container)
  (:export :bind-album
           :bind-image))
(in-package :datum.app.web.route.api)

(defun bind-album (app conf)
  (bind-route! app "/api/album/covers"
    ((:query "offset") (:query "count"))
    (lambda (offset count)
      (with-container (container conf)
        (let ((albums (datum.album:load-albums-by-range
                       (get-album-loader container)
                       (or offset 0)
                       (or count 500))))
          (mapcar #'datum.album:album-cover albums)))))
  (bind-route! app "/api/album/:id/overview"
    ((:param :id))
    (lambda (album-id)
      (with-container (container conf)
        (let ((album (car (datum.album:load-albums-by-ids
                           (get-album-loader container)
                           (list album-id)))))
          (datum.album:album-overview album
           (get-db container)
           (get-image-repository container)))))))

(defun bind-image (app conf)
  (bind-route! app "/api/image/:id"
    ((:param :id))
    (lambda (id)
      (with-container (container conf)
        (let ((image (car (datum.image:load-images-by-ids
                           (get-image-repository container)
                           (list id)))))
          (datum.image:image-path image))))
    :out #'as-file))
