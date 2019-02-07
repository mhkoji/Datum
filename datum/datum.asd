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

   (:file "album/thumbnail/entities")
   (:file "album/pictures/entities")
   (:file "album/pictures/db/mito")
   (:file "album/db/mito")

   (:file "image/db/mito")

   (:module :db
    :pathname "db"
    :components
    ((:file "db")
     (:file "mito")))

   (:file "container"))

  :depends-on (:babel
               :ironclad
               :mito
               :log4cl))
