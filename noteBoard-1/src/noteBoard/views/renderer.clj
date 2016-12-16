(ns noteBoard.views.renderer
  (:require [selmer.parser :as parser]
            [ring.util.http-response :refer [content-type ok]]))

(parser/set-resource-path! (clojure.java.io/resource "templates"))

(defn render
  [templateName & [params]]
  (content-type
    (ok
      (parser/render-file
        templateName
        (assoc params :page templateName)))
    "text/html; charset=utf-8"))
