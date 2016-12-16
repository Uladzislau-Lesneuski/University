(ns noteBoard.core
  (:use compojure.core
        ring.middleware.cookies)
  (:require [noteBoard.dal.db :as db]
            [noteBoard.views.view :as view]
            [noteBoard.dal.dao.user-data-access-object :as user-dao]
            [noteBoard.dal.dao.note-data-access-object :as note-dao]
            [noteBoard.dal.dao.group-data-access-object :as group-dao]
            [noteBoard.dal.dao.status-data-access-object :as stat-dao]
            [noteBoard.logic.services.user-service :as user-service-d]
            [noteBoard.logic.services.note-service :as note-service-d]
            [noteBoard.logic.services.group-service :as group-service-d]
            [noteBoard.logic.services.status-service :as stat-service-d]
            [noteBoard.logic.services.log-service :as ls]

            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [ring.middleware.session :as ss]
            [ring.util.response :as response]))

;Users
(def usr-dao (user-dao/->user-data-access-object db/db-map))
(def user-service (user-service-d/->user-service usr-dao))

;notes
(def note-dao (note-dao/->note-data-access-object))
(def note-servise (note-service-d/->note-service note-dao))

;Groups
(def grp-dao (group-dao/->group-data-access-object))
(def group-service (group-service-d/->group-service grp-dao))

;Statuses
(def stt-dao (stat-dao/->status-data-access-object))
(def stat-service (stat-service-d/->status-service stt-dao))

;Common
(def logged false)
(def stats (.get-all-items stat-service))


; SESSION ACTIONS
(defn add-user-to-session [response request user]
  (assoc response
    :session (-> (:session request)
                 (assoc :name (:name user))
                 (assoc :role (:role user))
                 (assoc :karma (:karma user))
                 (assoc :id (:id user)))))

(defn remove-user-from-session [response]
  (assoc response :session nil))

(defn get-user-from-session [request]
  (def user-info {:id (get-in request [:session :userid])
                  :email (get-in request [:session :email])
                  :role (get-in request [:session :role])})
  user-info)


; USER ACTIONS
(defn post-auth
  [request]
  (let [current-user (.sign-in user-service
                               (get-in request [:params :login])
                               (get-in request [:params :password]))
        request-login (get-in request [:params :login])]

    (if (= current-user nil)
      (println "We dont have such user")
      (do
        (ls/log-signin (:name current-user))
        (-> (response/redirect (str "user/" request-login))
            (add-user-to-session request current-user))))))

(defn add-user
  [request]
  (.add-item-over user-service (:params request)))

(defn show-user-page
  [session login]
  (do
      (view/render-user-page (.get-user-by-login user-service login) session)))

(defn logout
  []
  (-> (response/redirect "/home")
      (remove-user-from-session)))

(defn make-a-flush
  [session]
  (when (= (:role session) 0)
    (println "\n\n" session "\n\n")
    (ls/flush)
    (response/redirect "/home")))


; note ACTIONS
(defn show-noteBoard
  [session]
  (do
    (if (empty? session)
      (response/redirect "/home")
      (view/render-noteBoard-page (.get-all-items note-servise)
                                 (.get-all-items group-service)
                                 session))))

(defn show-noteBoard-edit
  [session id]
  (do
    (if (empty? session)
      (response/redirect "/home")
      (if (= id nil)
          (view/render-edit-note id
                                 (.get-all-items user-service)
                                 (.get-all-items group-service)
                                 stats
                                 session)
        (view/render-edit-note (.get-by-id note-servise id)
                               (.get-all-items user-service)
                               (.get-all-items group-service)
                               stats
                               session)))))

(defn delete-note
  [session id]
  (do
    (if (empty? session)
      (response/redirect "/home")
      (do
        (.delete-item note-servise id)
        (response/redirect "/noteboard")))))

(defn handle-note-edit-post
  [note-info session]
  (.edit-note note-servise note-info session)
  (response/redirect "/noteboard"))

(defn handle-note-add-post
  [note-info session]
  (println session)
  (.add-item note-servise note-info session)
  (response/redirect "/noteboard"))


; ROUTES
(defroutes app-routes
;Users
  (GET "/" [] (response/redirect "/home"))
  (GET "/home" [:as request] (view/render-home-page (:session request)))
  (GET "/auth" [] (view/render-signin-page))
  (POST "/auth" request (post-auth request))
  (GET "/signup" [] (view/render-signup-page))
  (POST "/signup" request (add-user request)
                          (response/redirect "/home"))
  (GET "/user/:login" [:as request login] (show-user-page (:session request) login))
  (POST "/user/logoff" [:as request] (logout))
  (GET "/flush" [:as request] (make-a-flush (:session request)))
  
;;notes          
  (GET "/noteboard" [:as request] (show-noteBoard (:session request)))
  (GET "/noteboard/note/:id" [:as request id] (show-noteBoard-edit (:session request) id))
  (GET "/noteboard/note/new" [:as request] (show-noteBoard-edit (:session request) nil))
  (GET "/noteboard/note/delete/:id" [:as request id] (delete-note (:session request) id))
  (POST "/noteedit" [:as request] (handle-note-edit-post (:params request) (:session request)))
  (POST "/noteadd" [:as request] (handle-note-add-post (:params request) (:session request)))

;;Groups
  (GET "/noteboard/group/new" [:as request] (view/render-edit-group nil (:session request)))
  (GET "/noteboard/group/delete/:id" [id] (.delete-item group-service id)
                                         (response/redirect "/noteboard"))

  (POST "/groupadd" request (.add-item-over group-service request)
                            (response/redirect "/noteboard"))

  (route/not-found "Page not found")
  (route/resources "/"))

(def engine
  (-> (handler/site app-routes)
      (middleware/wrap-json-body {:keywords? true})
      (ss/wrap-session)
      (wrap-cookies)))
