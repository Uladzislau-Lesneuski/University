(ns noteBoard.logic.services.log-service
  (:import (java.util Date)))

(def root-dir "C:\\Uni\\clojure\\taskdesk\\logs.txt")

(defn now []
  (.. (Date.) (toString)))

(def log-agent (agent (clojure.java.io/writer root-dir :append true)))

(defn write-out [writer message]
  (.write writer message)
  writer)

(defn flush-and-return [writer]
  (.flush writer)
  writer)

(defn log-message [logger message]
  (send logger write-out message))

(defn close [writer]
  (send writer #(.close %)))

(defn close-log [] )

(defn flush [writer]
  (send writer flush-and-return)
  writer)

(defn log-signin [user-name]
  (when (not-empty (str user-name))
    (do (log-message log-agent (str "User: (" user-name ") signed in on (" (now) ")\r\n"))
        (flush log-agent))))

(defn log-note-edit
  [user-name note-id edit-mode]
  (when (not-empty (str user-name))
    (do
      (if edit-mode
        (log-message log-agent (str "User: (" user-name ") edited note (" note-id ") on (" (now) ")\r\n"))
        (log-message log-agent (str "User: (" user-name ") added note (" note-id ") on (" (now) ")\r\n")))
      (flush log-agent))))
