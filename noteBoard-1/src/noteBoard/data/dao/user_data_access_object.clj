(ns noteBoard.data.dao.user-data-access-object
  (:require [noteBoard.data.protocols.user-db-protocol :as user-protocol]
            [noteBoard.data.protocols.common-db-protocol :as common-protocol]
            [noteBoard.data.models.user-model :as user-record]
            [clojure.java.jdbc :as jdbc]))

(deftype user-data-access-object [db-map]

  common-protocol/common-db-protocol

  (get-all-items
    [this]
    (into [] (jdbc/query db-map
                        ["SELECT id, name
                          FROM users"])))

  (add-item-over
    [this options]
    (jdbc/insert! db-map
                  :users
                  {:login     (:login options)
                   :password  (:password options)
                   :name      (:name options)
                   :email     (:email options)}))
  

  user-protocol/user-db-protocol

  (sign-in
    [this login password]
    (first (jdbc/query db-map
                      ["SELECT *
                        FROM users
                        WHERE login=? AND password=?" login password])))
  ;COUNT(*) as count
  (get-user-by-login
    [this login]
    (first (jdbc/query db-map
                       ["SELECT id, login, name, email, role, karma
                        FROM users
                        WHERE login=?" login]
                        :row-fn #(user-record/->user-record
                                  (:id %1)
                                  (:login %1)
                                  (:name %1)
                                  (:email %1)
                                  (:role %1)
                                  (:karma %1)))))
)
