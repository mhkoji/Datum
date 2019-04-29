(asdf:defsystem :datum-app-cli
  :serial t
  :pathname "src"
  :components
  ((:file "cli")
   (:file "album/album")
   (:file "album/add-albums")
   (:file "image")
   (:file "tag"))
  :depends-on (:datum))
