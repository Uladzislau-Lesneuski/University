(ns taskdesk.validation.task-validation
  (:require
    [clojure.string :refer :all]))
  ;(import java.util.regex.Matcher)
  ;(import java.util.regex.Pattern))

;(defn is-task-valid
;  [task]
;  (is-correct-date (:milestone task)))
;
;(defn is-len-in-borders
;  [str min max]
;  (and (> (count str) min) (< (count str) max)))
;
(defn is-correct-date? [date]
  (def p (java.util.regex.Pattern/compile "\\d{4}-\\d{2}-\\d{2}"))
  (def m (.matcher p date))
  (.matches m))

(defn is-len-in-interval? [str max min]
  (not (or (blank? str) (> (count str) max) (< (count str) min))))

(defn is-int-in-interval? [number max min]
  (let [int-number (new Integer number)]
    (and (<= int-number max ) (>= int-number min))))