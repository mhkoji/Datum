# Datum
Data repository written in Common Lisp

## datum-app-web

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
$ mkdir target
$ mkdir -p /tmp/datum/thubmanils/
$ sbcl
CL-USER> (defvar *conf*
           (datum.container:make-configure
            :id-generator
            (make-instance 'datum.id:sha256-3)
            :db-factory
            (make-instance 'datum.db.mito:mito-factory
                           :args (list :sqlite3
                                       :database-name
                                       "./target/db.sqlite3.bin"))
            :thumbnail-root
            "/tmp/datum/thubmanils/"))
CL-USER> (datum.app.cli:save-albums "./resources/contents/"
                                    :conf *conf*
                                    :initialize-data-p t)
CL-USER> (datum.app.web:start :conf *conf*)
```

Visit http://localhost:18888/albums
