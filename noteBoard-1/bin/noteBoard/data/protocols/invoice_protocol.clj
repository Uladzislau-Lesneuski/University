(ns noteBoard.data.protocols.invoice-protocol)

(defprotocol invoice-protocol
  (save-invoice [this inv-info])
  (watch-invoice [this user id])
  (get-invoices [this user]))