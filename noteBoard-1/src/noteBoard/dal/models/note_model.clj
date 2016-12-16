(ns noteBoard.dal.models.note-model)

(defrecord note-record
  [id
   author
   date
   title
   description
   milestone
   assignee
   status
   group])