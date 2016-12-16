(ns taskdesk.dal.protocols.user-db-protocol)

(defprotocol user-db-protocol
  (sign-in [this login password])
  (get-user-by-login [this login]))