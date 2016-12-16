(ns noteBoard.logic.services.status-service
  (:require [noteBoard.logic.protocols.common-service-protocol :as common-protocol]))

(deftype status-service [status-dao]

  common-protocol/common-service-protocol

  (get-all-items
    [this]
    (def response (.get-all-items status-dao))
    (println "\n STATUS ITEMS::::::::::::::::::::::::::::::::::::::::\n" response)
    response)

  (add-item
    [this options session]
    (.add-item status-dao options))

  )
