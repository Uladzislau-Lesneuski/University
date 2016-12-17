(ns noteBoard.data.dao.note-data-access-object
  (:require [noteBoard.data.protocols.common-db-protocol :as common-protocol]
            [noteBoard.data.protocols.note-db-protocol :as note-protocol]
            [noteBoard.data.models.note-model :as note-model]
            [clojure.java.jdbc :as jdbc]
            [noteBoard.data.db :as db]))

(deftype note-data-access-object []

  common-protocol/common-db-protocol

  (get-all-items
    [this]
    (into [] (jdbc/query db/db-map
                         ["SELECT n.id,
                                  u.name AS author,
                                  date,
                                  title,
                                  description,
                                  milestone,
                                  us.name AS assignee,
                                  n.status,
                                  n.group
                           FROM notes as n
                           JOIN users as u
                              ON u.id = n.author
                           JOIN users as us
                              ON us.id = n.assignee
                           JOIN notegroups as ng
                              ON ng.id = n.group"]
                         :row-fn #(note-model/->note-record
                                   (:id %1)
                                   (:author %1)
                                   (:date %1)
                                   (:title %1)
                                   (:description %1)
                                   (:milestone %1)
                                   (:assignee %1)
                                   (:status %1)
                                   (:group %1)))))

  (add-item
    [this options]
    (jdbc/execute! db/db-map
                   ["INSERT INTO notes (date,
                                        title,
                                        description,
                                        milestone,
                                        notes.status,
                                        author,
                                        assignee,
                                        notes.group)
                   values (?,
                           ?,
                           ?,
                           ?,
                           ?,
                           (SELECT id FROM users WHERE name=?),
                           (SELECT id FROM users WHERE name=?),
                           (SELECT id FROM notegroups WHERE name=?))"
                    (:date options)
                    (:title options)
                    (:description options)
                    (:milestone options)
                    (:status options)
                    (:author options)
                    (:assignee options)
                    (:group options)])
    
    (jdbc/execute! db/db-map
                   ["UPDATE users SET karma = karma + 1 WHERE name =?"
                   (:assignee options)]))

  note-protocol/note-db-protocol

  (get-by-id
    [this id]
    (first (jdbc/query db/db-map
                       ["SELECT n.id,
                                  u.name AS author,
                                  date,
                                  title,
                                  description,
                                  milestone,
                                  us.name AS assignee,
                                  n.status,
                                  ng.name AS \"group\"
                           FROM notes as n
                           JOIN users as u
                              ON u.id = n.author
                           JOIN users as us
                              ON us.id = n.assignee
                           JOIN notegroups as ng
                              ON ng.id = n.group
                           WHERE n.id=?" id]
                       :row-fn #(note-model/->note-record
                                 (:id %1)
                                 (:author %1)
                                 (:date %1)
                                 (:title %1)
                                 (:description %1)
                                 (:milestone %1)
                                 (:assignee %1)
                                 (:status %1)
                                 (:group %1)))))

  (edit-note
    [this note-opts]
    (jdbc/execute! db/db-map
                ["UPDATE notes as n
                JOIN users as u
                  ON u.name=?
                JOIN notegroups as ng
                  ON ng.name=?
                SET title=?,
                    description=?,
                    milestone=?,
                    n.status=?,
                    assignee=u.id,
                    n.group=tg.id
                WHERE n.id=?"
                 (:assignee note-opts)
                 (:group note-opts)
                 (:title note-opts)
                 (:description note-opts)
                 (:milestone note-opts)
                 (:status note-opts)
                 (Integer. (re-find #"[0-9]*" (:id note-opts)))]))

  (delete-item
    [this id]
    (jdbc/delete! db/db-map
                  :notes
                  ["id=?" id]))

  )
