;;; What a controller does is:
;;; 1. Receive an input
;;; 2. Prepare objects used for executing the controller task
;;; 3. Execute the task with the input and prepared objects
;;; 4. Send the output of the task in a formatted style
(defpackage :datum.web.route.api
  (:use :cl
        :datum.web.route
        :datum.app.container)
  (:export :route-api))
(in-package :datum.web.route.api)

(defun route-album (app conf)
  (route app "/api/album/covers"
   :args ((o (:query "offset")) (c (:query "count")))
   :perform (datum.web.app.album:covers conf o c))
  (route app "/api/album/search"
   :args ((keyword (:query "keyword")))
   :perform (datum.web.app.album:search conf keyword))
  (route app "/api/album/:id/overview"
   :args ((id (:param :id)))
   :perform (datum.web.app.album:overview
             conf (datum.id:from-string-short id)))
  (route app "/api/album/:id/tags"
   :args ((id (:param :id)))
   :perform (datum.web.app.album:tags
             conf (datum.id:from-string-short id)))
  (route app "/api/album/:id/tags"
   :method :put
   :args ((id (:param :id))
          (tag-ids (:query "tag_ids")))
   :perform (datum.web.app.album:set-tags
             conf (datum.id:from-string-short id) tag-ids)))

(defun route-image (app conf)
  (route app "/api/image/:id"
   :args ((id (:param :id)))
   :perform (datum.web.app.image:path
             conf (datum.id:from-string-short id))
   :output #'as-file))

(defun route-tag (app conf)
  (route app "/api/tags"
   :perform (datum.web.app.tag:tags conf))
  (route app "/api/tags"
   :args ((name (:query "name")))
   :perform (datum.web.app.tag:add-tag conf name)
   :method :put)

  (route app "/api/tag/:id"
    :method :delete
    :args ((tag-id (:param :id)))
    :perform (datum.web.app.tag:delete-tag conf tag-id))
  (route app "/api/tag/:id/albums"
    :args ((tag-id (:param :id)))
    :perform (datum.web.app.tag:album-covers conf tag-id)))

(defun route-api (app conf)
  (route app "/api/frequently-accessed/album/covers"
    :perform (datum.web.app.frequently-accessed:album-covers conf))
  (route-album app conf)
  (route-image app conf)
  (route-tag app conf))
