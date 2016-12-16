(ns noteBoard.logic.services.user-service
  (:require [noteBoard.logic.protocols.user-service-protocol :as user-protocol]
            [noteBoard.logic.protocols.common-service-protocol :as common-protocol]
            [noteBoard.dal.dao.user-data-access-object :as user-model]
            ))

(deftype user-service [user-model]

  common-protocol/common-service-protocol

  (get-all-items
    [this]
    (.get-all-items user-model))

  (add-item-over 
    [this options]
    (.add-item-over user-model options))

  user-protocol/user-service-protocol

  (sign-in [this login password]
    (.sign-in user-model login password))

  (get-user-by-login [this login]
    (def user (.get-user-by-login user-model login))
    (println user)
    user)

  )

