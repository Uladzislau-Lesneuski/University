(ns taskdesk.dal.dao.status-data-access-object
  (:require [taskdesk.dal.models.status-model :as status-model]
            [taskdesk.dal.protocols.common-db-protocol :as common-protocol]
            [taskdesk.dal.db :as db]
            [clojure.java.jdbc :as jdbc]))

(deftype status-data-access-object []

  common-protocol/common-db-protocol

  (get-all-items
    [this]
    (into [] (jdbc/query db/db-map
                         ["SELECT *
                         FROM status"])))

  (add-item
    [this options]
    (println options))
  )
