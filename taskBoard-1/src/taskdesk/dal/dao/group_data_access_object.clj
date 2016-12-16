(ns taskdesk.dal.dao.group-data-access-object
  (:require [taskdesk.dal.protocols.common-db-protocol :as common-protocol]
            [taskdesk.dal.protocols.group-db-protocol :as group-protocol]
            [taskdesk.dal.models.group-model :as group-model]
            [taskdesk.dal.db :as db]
            [clojure.java.jdbc :as jdbc]))

(deftype group-data-access-object []

  common-protocol/common-db-protocol

  (get-all-items
    [this]
    (into [] (jdbc/query db/db-map
                         ["SELECT *
                           FROM taskgroups"]
                         :row-fn #(group-model/->group-record
                                   (:id %1)
                                   (:name %1)))))

  (add-item-over
    [this options]
    (jdbc/insert! db/db-map
                  :taskgroups
                  {:name (:name options)}))

  group-protocol/group-db-protocol

  (delete-item
    [this id]
    (jdbc/delete! db/db-map
                  :taskgroups
                  ["id=?" id]))

  )
