(asdf:defsystem :datum
  :serial t
  :pathname "src"
  :components
  ((:file "id")

   (:module :album
    :pathname "album"
    :components
    ((:file "pictures/db/db")
     (:file "pictures/pictures")
     (:file "thumbnail/thumbnail")
     (:file "db/db")
     (:file "repository")
     (:file "album")))

   (:module :image
    :pathname "image"
    :components
    ((:file "db/db")
     (:file "image")))

   (:module :tag
    :pathname "tag"
    :components
    ((:file "db/db")
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
    ((:file "db/db")
     (:file "access-log")))

   (:file "album/thumbnail/entities")
   (:file "album/pictures/entities")
   (:file "album/pictures/db/mito")
   (:file "album/db/mito")

   (:file "image/db/mito")

   (:file "tag/db/mito")

   (:file "access-log/resources")
   (:file "access-log/db/mito")

   (:module :db
    :pathname "db"
    :components
    ((:file "db")
     (:file "mito")))

   (:file "container")

   (:module :app
    :pathname "app"
    :components
    ((:file "app")
     (:file "album/album")
     (:file "album/add-albums")
     (:file "image")
     (:file "tag")
     (:file "frequently-accessed"))))

  :depends-on (:babel
               :ironclad
               :mito
               :log4cl))
