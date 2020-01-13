(defpackage :datum.app.album.add-albums
  (:use :cl
        :datum.album
        :datum.stream
        :datum.fs.retrieve)
  (:import-from :datum.app.album
                :add-albums)
  (:import-from :datum.app
                :with-container)
  (:import-from :datum.image
                :save-images
                :create-images)
  (:import-from :alexandria
                :when-let))
(in-package :datum.app.album.add-albums)


(defun create-thumbnail (container image-path)
  (let ((thumbnail-path (datum.container:create-thumbnail-file
                         container
                         image-path)))
    (car (create-images (list thumbnail-path)))))

(defun create-source (container dir)
  (make-source :name
               (dir-path dir)
               :updated-at
               (local-time:universal-to-timestamp (dir-write-date dir))
               :thumbnail
               (when-let ((paths (dir-file-paths dir)))
                 (create-thumbnail container (car paths)))))

(defun add-albums (conf root-dir &key (sort-paths-fn #'identity))
  (with-container (container conf)
    (let ((dirs (stream-to-list (retrieve root-dir sort-paths-fn))))
      (let ((albums (create-albums
                     (mapcar (lambda (d)
                               (create-source container d))
                             dirs))))
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
                     collect (let ((images (create-images
                                            (dir-file-paths dir))))
                               (save-images container images)
                               (make-pictures-appending :album album
                                                        :entities images)))))
          (append-album-pictures container appendings)))))
  (values))
