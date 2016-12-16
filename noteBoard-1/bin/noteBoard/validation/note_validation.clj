(ns noteBoard.validation.note-validation
  (:require
    [clojure.string :refer :all]))

(defn is-correct-date? [date]
  (def p (java.util.regex.Pattern/compile "\\d{4}-\\d{2}-\\d{2}"))
  (def m (.matcher p date))
  (.matches m))

(defn is-len-in-interval? [str max min]
  (not (or (blank? str) (> (count str) max) (< (count str) min))))

(defn is-int-in-interval? [number max min]
  (let [int-number (new Integer number)]
    (and (<= int-number max ) (>= int-number min))))