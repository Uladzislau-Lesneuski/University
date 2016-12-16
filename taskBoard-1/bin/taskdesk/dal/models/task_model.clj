(ns taskdesk.dal.models.task-model)

(defrecord task-record
  [;;unchangable
     id
     author
     date
   ;;changable
     title
     description
     milestone
     assignee
     status
     group])