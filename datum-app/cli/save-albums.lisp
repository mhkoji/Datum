(defpackage :datum.app.cli.save-albums
  (:use :cl
        :datum.album
        :datum.stream
        :datum.fs.retrieve)
  (:export :execute)
  (:import-from :datum.image
                :save-images
                :create-images)
  (:import-from :alexandria
                :when-let))
(in-package :datum.app.cli.save-albums)

(defun create-thumbnail-file (thumbnail-file-fn thumbnail-source-path)
  (funcall thumbnail-file-fn thumbnail-source-path))

(defun create-thumbnail (thumbnail-source-path
                         &key thumbnail-file-fn id-generator)
  (let ((thumbnail-path (create-thumbnail-file thumbnail-file-fn
                                               thumbnail-source-path)))
    (car (create-images id-generator (list thumbnail-path)))))

(defun execute (root-dir &key db
                              image-repository
                              id-generator
                              sort-paths-fn
                              thumbnail-file-fn)
  (let ((dirs (stream-to-list (retrieve root-dir sort-paths-fn))))
    (let ((sources (mapcar
                    (lambda (dir)
                      (make-source
                       :name (dir-path dir)
                       :updated-at (dir-write-date dir)
                       :thumbnail
                       (when-let ((paths (dir-file-paths dir)))
                         (create-thumbnail (car paths)
                          :thumbnail-file-fn thumbnail-file-fn
                          :id-generator id-generator))))
                    dirs)))
      (let ((albums (create-albums id-generator sources)))
        ;; Delete existing albums if any
        (delete-albums db (mapcar #'album-id albums))
        ;; Save albums
        (save-albums db albums)
        (let ((thumbnails (remove nil (mapcar #'album-thumbnail
                                              albums))))
          (save-images image-repository thumbnails))
        ;; Append pictures
        (let ((appendings
               (loop for dir in dirs
                     for album in albums
                     collect
                       (let ((entities (create-images
                                        id-generator
                                        (dir-file-paths dir))))
                         (save-images image-repository entities)
                         (make-pictures-appending :album album
                                                  :entities entities)))))
          (append-album-pictures appendings db)))))
  (values))
