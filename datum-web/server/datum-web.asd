(asdf:defsystem :datum-web
  :serial t
  :pathname "src/cl"
  :components
  ((:module :app
    :pathname "app"
    :components
    ((:file "album")
     (:file "image")
     (:file "tag")
     (:file "frequently-accessed")))

   (:file "json")
   (:file "html")

   (:module :route
    :pathname "route"
    :components
    ((:file "route")
     (:file "api")
     (:file "asset")))

   (:file "web")
   (:file "bin"))
  :depends-on (:datum

               :cl-who
               :cl-arrows
               :clack
               :jsown
               :log4cl
               :lack
               :lack-middleware-static
               :ningle))
