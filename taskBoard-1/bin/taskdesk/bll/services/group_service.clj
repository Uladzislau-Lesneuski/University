(ns taskdesk.bll.services.group-service
  (:require [taskdesk.bll.protocols.common-service-protocol :as common-protocol]
            [taskdesk.bll.protocols.group-service-protocol :as group-protocol]))

(deftype group-service [group-dao]

  common-protocol/common-service-protocol

  (get-all-items
    [this]
    (def response (.get-all-items group-dao))
    (println "\n----------------GROUP ITEMS-------------\n" response)
    response)

  (add-item-over
    [this options]
    (def opts (:params options))
    (.add-item-over group-dao opts))

  group-protocol/group-protocol

  (delete-item
    [this id]
    (.delete-item group-dao id))
  )
