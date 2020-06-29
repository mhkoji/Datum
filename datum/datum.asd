(asdf:defsystem :datum
  :serial t
  :pathname "src"
  :components
  ((:file "id/uuid")
   ;(:file "id/sha256")

   (:module :image
    :pathname "image"
    :components
    ((:file "db")
     (:file "image")))

   (:module :album
    :pathname "album"
    :components
    ((:file "thumbnail/thumbnail")
     (:file "thumbnail/entities")
     (:file "pictures/db")
     (:file "pictures/pictures")
     (:file "db")
     (:file "album")))

   (:module :tag
    :pathname "tag"
    :components
    ((:file "db")
     (:file "tag")
     (:file "contents")))

   (:file "stream")

   (:module :fs
    :pathname "fs"
    :components
    ((:file "retrieve")
     (:file "thumbnail")))

   (:module :access-log
    :pathname "access-log"
    :components
    ((:file "db")
     (:file "access-log")
     (:file "resources")))

   (:module :db
    :pathname "db"
    :components
    ((:file "db")
     (:module :mito
      :pathname "mito"
      :components
      ((:file "mito")
       (:file "album")
       (:file "image")
       (:file "tag")
       (:file "access-log")))))

   (:module :app
    :pathname "app"
    :components
    ((:file "container")
     (:file "app"))))

  :depends-on (:babel
               :ironclad
               :mito
               :log4cl
               :uuid
               :cl-base64))
