(ns noteBoard.logic.protocols.common-service-protocol)

(defprotocol common-service-protocol
  (get-all-items [this])
  (add-item [this options session])
  (add-item-over [this options]))