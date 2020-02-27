(asdf:defsystem :datum-cli
  :serial t
  :pathname "src"
  :components
  ((:module :app
    :pathname "app"
    :components
    ((:file "add-albums"))))
  :depends-on (:datum))
