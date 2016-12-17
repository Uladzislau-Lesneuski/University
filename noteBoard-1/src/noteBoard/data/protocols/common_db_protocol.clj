(ns noteBoard.data.protocols.common-db-protocol)

(defprotocol common-db-protocol
  (get-all-items [this])
  (add-item [this options])
  (add-item-over [this options]))