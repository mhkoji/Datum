(defpackage :datum.app.cli
  (:use :cl :datum.container)
  (:export :save-albums))
(in-package :datum.app.cli)

(defun configure-thumbnail-file-fn (configure)
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

(defun save-albums (root-dir
                    &key (conf (datum.container:load-configure))
                         (sort-paths-fn #'identity)
                         (initialize-data-p nil))
  (with-container (c conf)
    (when initialize-data-p
      (datum.db:initialize (container-db c)))
    (datum.app.cli.save-albums:execute root-dir
     :db                (container-db c)
     :id-generator      (container-id-generator c)
     :sort-paths-fn     sort-paths-fn
     :thumbnail-file-fn (container-thumbnail-file-fn c))))
