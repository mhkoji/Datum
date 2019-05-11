(defpackage :datum.app.album.add-albums
  (:use :cl
        :datum.album
        :datum.stream
        :datum.fs.retrieve)
  (:import-from :datum.app.album
                :add-albums)
  (:import-from :datum.app
                :configure-id-generator
                :configure-thumbnail-root
                :with-container)
  (:import-from :datum.image
                :save-images
                :create-images)
  (:import-from :alexandria
                :when-let))
(in-package :datum.app.album.add-albums)

(defun get-thumbnail-file-fn (conf)
  (labels ((make-thumbnail-path (source-path)
             (format nil "~Athumbnail$~A"
                     (configure-thumbnail-root conf)
                     (cl-ppcre:regex-replace-all "/" source-path "$")))
           (make-thumbnail-file (source-path)
             (log:debug "Creating thumbnail for: ~A" source-path)
             (let ((thumbnail-path (make-thumbnail-path source-path)))
               (datum.fs.thumbnail:ensure-exists thumbnail-path source-path)
               thumbnail-path)))
    #'make-thumbnail-file))

(defun create-thumbnail-file (thumbnail-file-fn thumbnail-source-path)
  (funcall thumbnail-file-fn thumbnail-source-path))

(defun create-thumbnail (source-path
                         &key thumbnail-file-fn id-generator)
  (let ((thumbnail-path (create-thumbnail-file thumbnail-file-fn
                                               source-path)))
    (car (create-images id-generator (list thumbnail-path)))))

(defun add-albums (root-dir &key (conf (datum.app:load-configure))
                                 (sort-paths-fn #'identity))
  (let ((dirs (stream-to-list (retrieve root-dir sort-paths-fn)))
        (id-generator (configure-id-generator conf))
        (thumbnail-file-fn (get-thumbnail-file-fn conf)))
    (let ((sources (mapcar
                    (lambda (dir)
                      (make-source :name (dir-path dir)
                                   :updated-at
                                   (local-time:universal-to-timestamp
                                    (dir-write-date dir))
                                   :thumbnail
                                   (when-let ((paths (dir-file-paths dir)))
                                     (create-thumbnail (car paths)
                                      :thumbnail-file-fn thumbnail-file-fn
                                      :id-generator id-generator))))
                    dirs)))
      (let ((albums (create-albums id-generator sources)))
        (with-container (container conf)
          ;; Delete existing albums if any
          (delete-albums container (mapcar #'album-id albums))
          ;; Save albums
          (let ((thumbnails (remove nil (mapcar #'album-thumbnail albums))))
            (save-images container thumbnails))
          (save-albums container albums)
          ;; Append pictures
          (let ((appendings
                 (loop for dir in dirs
                       for album in albums
                       collect
                        (let ((entities (create-images
                                         id-generator
                                         (dir-file-paths dir))))
                          (save-images container entities)
                          (make-pictures-appending :album album
                                                   :entities entities)))))
            (append-album-pictures container appendings))))))
  (values))
