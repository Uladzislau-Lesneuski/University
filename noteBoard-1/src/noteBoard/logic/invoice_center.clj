(ns noteBoard.logic.invoice-center
  (:require [noteBoard.data.dao.invoice-dao :as dao]))

(def invoice-center (atom {}))
(def inv-dao (dao/->invoice-dao))

(defn find-index
  [arr id]
  (for [item arr
        :when (= (str (:id item)) id)]
    (.indexOf arr item)))

(defn del-item-from-vec-by-index
  [vvector index]
  (let [length (count vvector)]
    (if (empty? vvector)
      ;empty vector
      vvector
      ;not empty vector
      (if (= index (dec length))
        ;last item
        (subvec vvector 0 index)
        ;not last item
        (if (= index 0)
          ;first item
          (subvec vvector 1)
          ;not first item
          (vec (concat (subvec vvector 0 index) (subvec vvector (inc index))))
          )
        ))
    ))

(defn add-invoice
  [user text]
  (let [user-invoices (into [] ((keyword user) @invoice-center))
        new-index (if (not-empty user-invoices)
                    (inc (:id (apply max-key :id user-invoices)))
                    0)
        new-invoice {:id new-index :text text}]
    (.save-invoice inv-dao {:user user :id new-index :text text})
    (swap! invoice-center conj {(keyword user) (conj user-invoices new-invoice)})
    (println @invoice-center)))

(defn get-invoices
  [user]
  (if (not-empty (str user))
    (into [] ((keyword user) @invoice-center))
    nil))

(defn remove-invoice
  [user id]
  (when (not-empty (str user))
    (let [user-invoices ((keyword user) @invoice-center)
          delete-index (first (find-index user-invoices id))
          user-invoices (del-item-from-vec-by-index user-invoices delete-index)
          ]
      (.watch-invoice inv-dao user id)
      (swap! invoice-center conj {(keyword user) user-invoices})
      (println @invoice-center)
      )
    ))

(defn remove-all-for-user
  [user]
  (swap! invoice-center conj {(keyword user) nil}))

(defn handle-sign-in
  [user]
  (let [user-invoices (into [] ((keyword user) @invoice-center))]
    (when (empty? user-invoices)
      (swap! invoice-center conj {(keyword user) (.get-invoices inv-dao user)})
      (println @invoice-center))
    ))