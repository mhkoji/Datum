(asdf:defsystem :datum
  :serial t
  :pathname "src"
  :components
  ((:file "id/uuid")
   ;(:file "id/sha256")

   (:module :album
    :pathname "album"
    :components
    ((:file "thumbnail/thumbnail")
     (:file "db/db")
     (:file "repository")))

   (:module :image
    :pathname "image"
    :components
    ((:file "db/db")
     (:file "image")))

   (:module :tag
    :pathname "tag"
    :components
    ((:file "db/db")
     (:file "tag")))

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

   (:file "db/db")

   (:file "album/pictures/db/db")
   (:file "album/pictures/pictures")
   (:file "album/album")
   (:file "album/thumbnail/entities")
   (:file "access-log/resources")

   (:file "db/mito")
   (:file "album/pictures/db/mito")
   (:file "album/db/mito")
   (:file "tag/contents")
   (:file "image/db/mito")
   (:file "tag/db/mito")
   (:file "access-log/db/mito")

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
               :log4cl
               :uuid
               :cl-base64))
