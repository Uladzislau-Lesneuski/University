(ns noteBoard.views.view
  (:use hiccup.page
        hiccup.element)
  (:require [noteBoard.views.renderer :as renderer]
            [noteBoard.logic.invoice-center :as ic]))

(defn render-home-page
  [session]
  (let [invoices (ic/get-invoices (:name session))
        invoice-count (count invoices)]
    (renderer/render "home.html" {:docs "document" :session session :invoices invoice-count}))

(defn render-signin-page
  []
  (renderer/render "auth.html"))

(defn render-user-page
  [user session]
  (let [invoices (ic/get-invoices (:name session))
        invoice-count (count invoices)]
    (renderer/render "user.html" {:user user :session session :invoices invoice-count})))

(defn render-signup-page
  []
  (renderer/render "signup.html"))

(defn render-noteBoard-page
  [tasks groups user]
  (let [invoices (ic/get-invoices (:name user))
        invoice-count (count invoices)]
    (renderer/render "noteboard.html" {:tasks tasks :groups groups :session user :invoices invoice-count})))

(defn render-edit-note
  [tasks users groups stats session]
  (renderer/render "noteEdit.html" {:notes tasks :users users :groups groups :stats stats :session session}))

(defn render-edit-group
  [group session]
  (let [invoices (ic/get-invoices (:name session))
        invoice-count (count invoices)]
    (renderer/render "groupedit.html" {:group group :session session :invoices invoice-count})))

(defn render-invoice-page
  [session]
  (let [invoices (ic/get-invoices (:name session))
        invoice-count (count invoices)]
    (renderer/render "invoices.html" {:invoices invoice-count :invoice-list invoices :session session}))))
