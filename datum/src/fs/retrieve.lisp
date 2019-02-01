(defpackage :datum.fs.retrieve
  (:use :cl :datum.stream)
  (:export :dir-path
           :dir-file-paths
           :dir-write-date
           :retrieve))
(in-package :datum.fs.retrieve)

(defun files-and-subdirectories (dir)
  (let ((files nil)
        (subdirectories nil))
    (dolist (child-path (cl-fad:list-directory dir))
      (if (cl-fad:directory-pathname-p child-path)
          (push child-path subdirectories)
          (push (namestring child-path) files)))
    (list (nreverse files) subdirectories)))

(defstruct dir path file-paths write-date)

(defun retrieve (root sort-paths-fn)
  (destructuring-bind (files subdirs) (files-and-subdirectories root)
    (stream-concat
     (when files
       (stream-from-list
        (list (make-dir :path (namestring root)
                        :file-paths (funcall sort-paths-fn
                                             (mapcar #'namestring files))
                        :write-date (file-write-date root)))))
     (stream-flat-map (lambda (p)
                        (retrieve p sort-paths-fn))
                      (stream-from-list subdirs)))))
