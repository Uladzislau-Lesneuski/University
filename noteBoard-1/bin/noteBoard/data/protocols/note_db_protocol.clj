(ns noteBoard.data.protocols.note-db-protocol)

(defprotocol note-db-protocol
  (edit-note [this note-opts])
  (get-by-id [this id])
  (delete-item [this id]))
