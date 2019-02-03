(defpackage :datum.app.web.html
  (:use :cl)
  (:export :main))
(in-package :datum.app.web.html)

(defun main (js-src)
  (cl-who:with-html-output-to-string (s nil :prologue t)
    (:head
     (:meta :charset "utf-8")
     (:title "Datum")
     (:link
      :rel "stylesheet"
      :type "text/css"
      :href "/resources/css/main.css")
     (:link
      :rel "stylesheet"
      :type "text/css"
      :href "/resources/third_party/bootstrap-4.0.0/dist/css/bootstrap.min.css")
     (:link
      :rel "stylesheet"
      :href "/resources/third_party/open-iconic-1.1.0/font/css/open-iconic-bootstrap.css")

     (:body
     (:div :id "app")
     ; Main Javascript must be loaded after the body was rendered.
     (cl-who:htm (:script :type "text/javascript" :src js-src))
     (:script
      :type "text/javascript"
      :src "https://code.jquery.com/jquery-2.2.2.min.js"
      :integrity "sha256-36cp2Co+/62rEAAYHLmRCPIych47CvdM+uTBJwSzWjI="
      :crossorigin "anonymous")
     (:script
      :type "text/javascript"
      :src "/resources/third_party/bootstrap-4.0.0/dist/js/bootstrap.bundle.min.js")))))
