(ns noteBoard.data.dao.invoice-dao
  (:require [clojure.java.jdbc :as jdbc]
            [noteBoard.data.protocols.invoice-protocol :as ip]
            [noteBoard.data.db :as db]))

(deftype invoice-dao []

  ip/invoice-protocol

  (save-invoice
    [this inv-info]
    (jdbc/insert! db/db-map
                  :invoices
                  {:user (:user inv-info)
                   :localid (:id inv-info)
                   :text (:text inv-info)}))

  (watch-invoice
    [this user id]
    (println "\n\n" user (type user) id (type id))
    (jdbc/execute! db/db-map
                   ["UPDATE invoices SET watched=1
                    WHERE user=? AND localid=?"
                    user
                    (Integer. (re-find #"[0-9]*" id))]))

  (get-invoices
    [this user]
    (into [] (jdbc/query db/db-map
                       ["SELECT localid as id, text FROM invoices
                        WHERE user=? AND watched=0"
                        user])))
  )