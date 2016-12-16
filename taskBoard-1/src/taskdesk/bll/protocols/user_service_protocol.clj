(ns taskdesk.bll.protocols.user-service-protocol)

(defprotocol user-service-protocol
  (sign-in [this login password])
  (get-user-by-login [this login]))
