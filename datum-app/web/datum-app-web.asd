(asdf:defsystem :datum-app-web
  :serial t
  :pathname "src/cl"
  :components
  ((:file "json")
   (:file "html")

   (:module :route
    :pathname "route"
    :components
    ((:file "route")
     (:file "api")
     (:file "asset")))

   (:file "web"))
  :depends-on (:datum

               :cl-who
               :cl-arrows
               :clack
               :jsown
               :log4cl
               :lack
               :lack-middleware-static
               :ningle))
