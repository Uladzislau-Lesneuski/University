(ns taskdesk.bll.protocols.task-service-protocol)

(defprotocol task-service-protocol
  (edit-task [this task-opts session])
  (get-by-id [this id])
  (delete-item [this id]))
