(ns taskdesk.bll.protocols.group-service-protocol)

(defprotocol group-protocol
  (delete-item [this id]))