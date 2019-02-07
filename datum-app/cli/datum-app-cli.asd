(asdf:defsystem :datum-app-cli
  :serial t
  :pathname "src"
  :components
  ((:file "save-albums")
   (:file "cli"))
  :depends-on (:datum))
