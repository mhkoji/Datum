# Datum
Data repository written in Common Lisp

## datum-web

Web application for Datum.

![webapp](https://github.com/mhkoji/datum/raw/master/imgs/webapp.png)

### Start

#### Frontend

```
$ git submodule update --init
$ cd datum-app/web
$ make
```

#### Server-side

```
$ cd datum-app/web
$ mkdir -p ./target/thumbnails
$ sbcl
CL-USER> (ql:quickload :datum-cli))
CL-USER> (defvar *conf*
           (datum.app:make-configure
            :id-generator
            (make-instance 'datum.id:sha256-3)
            :db-factory
            (make-instance 'datum.db.mito:mito-factory
                           :args (list :sqlite3
                                       :database-name
                                       "./target/db.sqlite3"))
            :thumbnail-root
            (merge-pathnames "./target/thumbnails/")))
CL-USER> (datum.cli.app.add-albums:run *conf* "./resources/contents/")
CL-USER> (datum.app:initialize *conf*)
CL-USER> (ql:quickload :datum-web)
CL-USER> (datum.web:start :conf *conf*)
```

Visit http://localhost:18888/albums
