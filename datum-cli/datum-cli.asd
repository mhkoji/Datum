(asdf:defsystem :datum-web
  :serial t
  :pathname "src/cl"
  :components
  ((:module :app
    :pathname "app"
    :components
    ((:file "album/add-albums"))))
  :depends-on (:datum))
