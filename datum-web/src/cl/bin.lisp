(defpackage :datum.web.bin
  (:use :cl))
(in-package :datum.web.bin)

(defun main (argv)
  (log:info "argv: ~A" argv)
  (let ((hash (alexandria:plist-hash-table argv :test #'equal))
        (start-args nil))

    (setq start-args (list :use-thread nil))

    (alexandria:when-let ((port (gethash "--port" hash)))
      (setq start-args (list* :port (parse-integer port)
                              start-args)))

    (alexandria:when-let ((conf-path (gethash "--conf-path" hash)))
      (let ((conf (datum.app:load-configure conf-path)))
        (setq start-args (list* :conf conf
                                start-args))))

    (log:info "args: ~A" start-args)

    (apply #'datum.web:start start-args)))

#+sbcl
(progn
  (export 'sbcl-main)
  (defun sbcl-main ()
    (main (cdr sb-ext:*posix-argv*))))
