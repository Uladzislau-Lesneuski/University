(ns taskdesk.dal.models.user-model)

(defrecord user-record
  [id
   login
   name
   email
   role
   karma])