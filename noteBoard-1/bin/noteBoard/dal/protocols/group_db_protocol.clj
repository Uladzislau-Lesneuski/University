(ns noteBoard.dal.protocols.group-db-protocol)

(defprotocol group-db-protocol
  (delete-item [this id]))