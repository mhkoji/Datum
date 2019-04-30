;;; What a controller does is:
;;; 1. Receive an input
;;; 2. Prepare objects used for executing the controller task
;;; 3. Execute the task with the input and prepared objects
;;; 4. Send the output of the task in a formatted style
(defpackage :datum.app.web.route.api
  (:use :cl
        :datum.app.web.route
        :datum.container)
  (:export :bind-all))
(in-package :datum.app.web.route.api)

(defun bind-album (app conf)
  (bind-route! app "/api/album/covers"
    ((:query "offset") (:query "count"))
    (lambda (o c)
      (datum.app.cli.album:covers conf o c)))
  (bind-route! app "/api/album/:id/overview"
    ((:param :id))
    (lambda (id)
      (datum.app.cli.album:overview conf id)))
  (bind-route! app "/api/album/:id/tags"
    ((:param :id))
    (lambda (id)
      (datum.app.cli.album:tags conf id)))
  (bind-route! app "/api/album/:id/tags"
    ((:param :id) (:query "tag_ids"))
    (lambda (id tag-ids)
      (datum.app.cli.album:set-tags conf id tag-ids))
    :method :put))

(defun bind-image (app conf)
  (bind-route! app "/api/image/:id"
    ((:param :id))
    (lambda (id)
      (datum.app.cli.image:path conf id))
    :out #'as-file))

(defun bind-tag (app conf)
  (bind-route! app "/api/tags"
    ()
    (lambda ()
      (datum.app.cli.tag:tags conf)))
  (bind-route! app "/api/tags"
    ((:query "name"))
    (lambda (name)
      (datum.app.cli.tag:add-tag conf name))
    :method :put)

  (bind-route! app "/api/tag/:id"
    ((:param :id))
    (lambda (tag-id)
      (datum.app.cli.tag:delete-tag conf tag-id))
    :method :delete)
  (bind-route! app "/api/tag/:id/albums"
    ((:param :id))
    (lambda (tag-id)
      (datum.app.cli.tag:album-covers conf tag-id))))

(defun bind-all (app conf)
  (bind-route! app "/api/frequently-accessed/album/covers"
    ()
    (lambda ()
      (datum.app.cli.frequently-accessed:album-covers conf)))
  (bind-album app conf)
  (bind-image app conf)
  (bind-tag app conf))
