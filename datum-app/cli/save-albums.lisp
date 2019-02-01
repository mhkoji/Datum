(defpackage :datum.app.cli.save-albums
  (:use :cl
        :datum.album
        :datum.stream
        :datum.fs.retrieve)
  (:export :execute)
  (:import-from :datum.image
                :save-images))
(in-package :datum.app.cli.save-albums)

(defun create-thumbnail-file (thumbnail-file-fn thumbnail-source-path)
  (funcall thumbnail-file-fn thumbnail-source-path))

(defun save-thumbnail (thumbnail-source-path &key thumbnail-file-fn
                                                  id-generator
                                                  image-repository)
  (let ((thumbnail-path (create-thumbnail-file
                         thumbnail-file-fn
                         thumbnail-source-path)))
    (car (save-images (list thumbnail-path) image-repository id-generator))))

(defun execute (root-dir &key db
                              id-generator
                              sort-paths-fn
                              thumbnail-file-fn)
  (let ((dirs (stream-to-list
               (retrieve root-dir sort-paths-fn)))
        (image-repos (datum.image:make-repository :db db)))
    (let ((sources (mapcar
                    (lambda (dir)
                      (let ((thumbnail
                             (save-thumbnail (car (dir-file-paths dir))
                              :thumbnail-file-fn thumbnail-file-fn
                              :image-repository image-repos
                              :id-generator id-generator)))
                        (make-source :name (dir-path dir)
                                     :thumbnail thumbnail
                                     :updated-at (dir-write-date dir))))
                    dirs)))
      (let ((albums (create-albums id-generator sources)))
        ;; Delete existing albums if any
        (delete-albums db image-repos (mapcar #'album-id albums))
        ;; Save albums
        (save-albums db albums)
        ;; append contents
        (let ((appendings
               (loop for dir in dirs
                     for album in albums
                     for contents = (let ((paths (dir-file-paths dir)))
                                      (save-images paths db id-generator))
                     collect (make-contents-appending :album album
                                                      :contents contents))))
          (append-album-contents db appendings)))))
  (values))
