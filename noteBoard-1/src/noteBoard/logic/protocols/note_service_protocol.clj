(ns noteBoard.logic.protocols.note-service-protocol)

(defprotocol note-service-protocol
  (edit-note [this note-opts session])
  (get-by-id [this id])
  (delete-item [this id]))
