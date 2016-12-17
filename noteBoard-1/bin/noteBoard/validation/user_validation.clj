(ns noteBoard.validation.user-validation
  (:require
    [clojure.string :refer :all]))

(defn is-blanc-fields? [login password name email]
  (not (or (blank? login) (blank? password) (blank? name) (blank? email))))