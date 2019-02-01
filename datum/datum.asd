(asdf:defsystem :datum
  :serial t
  :pathname "src"
  :components
  ((:file "id")

   (:module :album
    :pathname "album"
    :components
    ((:file "contents/db/db")
     (:file "contents/contents")
     (:file "thumbnail/thumbnail")
     (:file "db/db")
     (:file "repository")
     (:file "album")))

   (:module :image
    :pathname "image"
    :components
    ((:file "db/db")
     (:file "image")))

   (:file "album/contents/db/mito")
   (:file "album/db/mito")
   (:file "image/db/mito"))

  :depends-on (:babel
               :ironclad
               :mito))
