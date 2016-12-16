(ns taskdesk.dal.protocols.task-db-protocol)

(defprotocol task-db-protocol
  (edit-task [this task-opts])
  (get-by-id [this id])
  (delete-item [this id]))
