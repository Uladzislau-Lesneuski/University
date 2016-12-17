(ns noteBoard.logic.services.note-service
  (:require [noteBoard.logic.protocols.note-service-protocol :as note-protocol]
            [noteBoard.logic.protocols.common-service-protocol :as common-protocol]
            [noteBoard.logic.services.log-service :as ls]
            [noteBoard.logic.invoice-center :as ic]
            [noteBoard.validation.note-validation :refer :all]
            [noteBoard.data.dao.note-data-access-object :as note-dao])
  (import java.util.Date)
  (import java.text.SimpleDateFormat))

(deftype note-service [note-dao]

  common-protocol/common-service-protocol

  (get-all-items
    [this]
    (def response (.get-all-items note-dao))
    (println "\n note ITEMS::::::::::::::::::::::::::::::::::::::::::\n" response)
    response)

  (add-item
    [this options session]
    (let [options (assoc options :date (.format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss")(Date.)))
          options (assoc options :author (:name session))]
      (ls/log-note-edit (:author options) (:title options) false)
      (ic/add-invoice (:assignee options) (str "User ("(:name session)") assigned note: ("
                                               (:title options)") for you."))
      (if (is-correct-date? (:milestone options))
        (do
          (ls/log-note-edit (:author options) (:title options) false)
          (.add-item note-dao options))
        (let [options (assoc options :milestone nil)]
          (do
            (ls/log-note-edit (:author options) (:title options) false)
            (.add-item note-dao options))))))

  note-protocol/note-service-protocol

  (edit-note
    [this note-opts session]
    (if (is-correct-date? (:milestone note-opts))
      (do
        
        (ic/add-invoice (:assignee note-opts) (str "User ("(:name session)") changed your/assigned you to/disassigned you from note: ("
                                                   (:title note-opts)")"))
        (ls/log-note-edit (:name session) (:title note-opts) true)
        (.edit-note note-dao note-opts))
      (println "Incorrect date")
      )
  )

  (get-by-id
    [this id]
    (.get-by-id note-dao id))

  (delete-item
 [this id session]
     (do
       (ls/log-note-delete (:name session) id)
       (.delete-item note-dao id))))
