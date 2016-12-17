(ns noteBoard.data.dao.status-data-access-object
  (:require [noteBoard.data.models.status-model :as status-model]
            [noteBoard.data.protocols.common-db-protocol :as common-protocol]
            [noteBoard.data.db :as db]
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
