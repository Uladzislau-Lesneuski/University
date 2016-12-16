(ns taskdesk.bll.invoice-center)

(def invoice-center (atom {}))

(defn add-invoice
  [user text]
  (let [user-invoices ((keyword user) @invoice-center)]
    (swap! invoice-center conj {(keyword user) (conj user-invoices text)})
    (println @invoice-center)))