(ns noteBoard.dal.dao.group-data-access-object
  (:require [noteBoard.dal.protocols.common-db-protocol :as common-protocol]
            [noteBoard.dal.protocols.group-db-protocol :as group-protocol]
            [noteBoard.dal.models.group-model :as group-model]
            [noteBoard.dal.db :as db]
            [clojure.java.jdbc :as jdbc]))

(deftype group-data-access-object []

  common-protocol/common-db-protocol

  (get-all-items
    [this]
    (into [] (jdbc/query db/db-map
                         ["SELECT *
                           FROM notegroups"]
                         :row-fn #(group-model/->group-record
                                   (:id %1)
                                   (:name %1)))))

  (add-item-over
    [this options]
    (jdbc/insert! db/db-map
                  :notegroups
                  {:name (:name options)}))

  group-protocol/group-db-protocol

  (delete-item
    [this id]
    (jdbc/delete! db/db-map
                  :notegroups
                  ["id=?" id]))

  )
