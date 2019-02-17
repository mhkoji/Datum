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
           :bind-image
           :bind-tag))
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
  (labels ((load-album-by-id (loader album-id)
             (car (datum.album:load-albums-by-ids
                   loader
                   (list album-id)))))
    (bind-route! app "/api/album/:id/overview"
      ((:param :id))
      (lambda (album-id)
        (with-container (container conf)
          (let ((album (load-album-by-id (get-album-loader container)
                                         album-id)))
            (datum.album:album-overview album
             (get-db container)
             (get-image-repository container))))))
    (bind-route! app "/api/album/:id/tags"
      ((:param :id))
      (lambda (album-id)
        (with-container (container conf)
          (let ((album (load-album-by-id (get-album-loader container)
                                         album-id)))
            (datum.tag:content-tags album (get-db container))))))
    (bind-route! app "/api/album/:id/tags"
      ((:param :id) (:query "tag_ids"))
      (lambda (album-id tag-ids)
        (with-container (container conf)
          (let ((db (get-db container)))
            (let ((album (load-album-by-id (get-album-loader container)
                                           album-id))
                  (tags (datum.tag:load-tags-by-ids db tag-ids)))
              (datum.tag:content-set-tags album tags db))))
        (values))
      :method :put)))


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


(defun bind-tag (app conf)
  (bind-route! app "/api/tags"
    ()
    (lambda ()
      (with-container (container conf)
        (datum.tag:load-tags-by-range (get-db container) 0 50))))
  (bind-route! app "/api/tags"
    ((:query "name"))
    (lambda (name)
      (with-container (container conf)
        (datum.tag:save-tag (get-db container) name)))
    :method :put)

  (bind-route! app "/api/tag/:id"
    ((:param :id))
    (lambda (tag-id)
      (with-container (container conf)
        (datum.tag:delete-tag (get-db container) tag-id)))
    :method :delete)
  (bind-route! app "/api/tag/:id/albums"
    ((:param :id))
    (lambda (tag-id)
      (with-container (container conf)
        (let ((tag (car (datum.tag:load-tags-by-ids (get-db container)
                                                    (list tag-id)))))
          (let ((albums (datum.tag:tag-contents tag
                         (get-db container)
                         (get-album-loader container))))
            (mapcar #'datum.album:album-cover albums)))))))
