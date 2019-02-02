(defpackage :datum.app.cli
  (:use :cl :datum.container)
  (:export :save-albums))
(in-package :datum.app.cli)

(defun save-albums (root-dir
                    &key (conf (load-configure))
                         (sort-paths-fn #'identity)
                         (initialize-data-p nil))
  (with-container (c conf)
    (when initialize-data-p
      (datum.db:initialize (get-db c)))
    (datum.app.cli.save-albums:execute root-dir
     :db                (get-db c)
     :image-repository  (get-image-repository c)
     :id-generator      (get-id-generator c)
     :sort-paths-fn     sort-paths-fn
     :thumbnail-file-fn (get-thumbnail-file-fn c))))
