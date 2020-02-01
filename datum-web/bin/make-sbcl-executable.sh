#!/bin/bash

ros run \
    -s datum-web \
    -e "(sb-ext:save-lisp-and-die \"datum-web\" :toplevel #'datum.web.bin:sbcl-main :executable t)"
