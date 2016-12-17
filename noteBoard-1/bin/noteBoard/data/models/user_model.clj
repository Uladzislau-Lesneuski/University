(ns noteBoard.data.models.user-model)

(defrecord user-record
  [id
   login
   name
   email
   role
   karma])