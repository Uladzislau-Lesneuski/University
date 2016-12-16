(ns taskdesk.core
  (:use compojure.core
        ring.middleware.cookies)
  (:require [taskdesk.dal.db :as db]
            [taskdesk.views.view :as view]
            [taskdesk.dal.dao.user-data-access-object :as user-dao]
            [taskdesk.dal.dao.task-data-access-object :as task-dao]
            [taskdesk.dal.dao.group-data-access-object :as group-dao]
            [taskdesk.dal.dao.status-data-access-object :as stat-dao]
            [taskdesk.bll.services.user-service :as user-service-d]
            [taskdesk.bll.services.task-service :as task-service-d]
            [taskdesk.bll.services.group-service :as group-service-d]
            [taskdesk.bll.services.status-service :as stat-service-d]
            [taskdesk.bll.services.log-service :as ls]


            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [ring.middleware.session :as ss]
            [ring.util.response :as response]))

;Users
(def usr-dao (user-dao/->user-data-access-object db/db-map))
(def user-service (user-service-d/->user-service usr-dao))

;Tasks
(def tsk-dao (task-dao/->task-data-access-object))
(def task-servise (task-service-d/->task-service tsk-dao))

;Groups
(def grp-dao (group-dao/->group-data-access-object))
(def group-service (group-service-d/->group-service grp-dao))

;Statuses
(def stt-dao (stat-dao/->status-data-access-object))
(def stat-service (stat-service-d/->status-service stt-dao))

;Common
(def logged false)
(def stats (.get-all-items stat-service))


;; ------------------------ SESSION ACTIONS -------------------------
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


;; ---------------------------- USER ACTIONS ----------------------------
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
  ;(ls/close-log)
  (-> (response/redirect "/home")
      (remove-user-from-session)))

(defn make-a-flush
  [session]
  (when (= (:role session) 0)
    (println "\n\n" session "\n\n")
    (ls/flush)
    (response/redirect "/home")))


;; ----------------------------- TASK ACTIONS ----------------------------
(defn show-taskdesk
  [session]
  (do
    (if (empty? session)
      (response/redirect "/home")
      (view/render-taskdesk-page (.get-all-items task-servise)
                                 (.get-all-items group-service)
                                 session))))

(defn show-taskdesk-edit
  [session id]
  (do
    (if (empty? session)
      (response/redirect "/home")
      (if (= id nil)
          (view/render-edit-task id
                                 (.get-all-items user-service)
                                 (.get-all-items group-service)
                                 stats
                                 session)
        (view/render-edit-task (.get-by-id task-servise id)
                               (.get-all-items user-service)
                               (.get-all-items group-service)
                               stats
                               session)))))

(defn delete-task
  [session id]
  (do
    (if (empty? session)
      (response/redirect "/home")
      (do
        (.delete-item task-servise id)
        (response/redirect "/taskdesk")))))

(defn handle-task-edit-post
  [task-info session]
  (.edit-task task-servise task-info session)
  (response/redirect "/taskdesk"))

(defn handle-task-add-post
  [task-info session]
  (println session)
  (.add-item task-servise task-info session)
  (response/redirect "/taskdesk"))


;; --------------------------------- ROUTES ------------------------------
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
  
;;Tasks          
  (GET "/taskdesk" [:as request] (show-taskdesk (:session request)))
  (GET "/taskdesk/task/:id" [:as request id] (show-taskdesk-edit (:session request) id))
  (GET "/taskdesk/task/new" [:as request] (show-taskdesk-edit (:session request) nil))
  (GET "/taskdesk/task/delete/:id" [:as request id] (delete-task (:session request) id))
  (POST "/taskedit" [:as request] (handle-task-edit-post (:params request) (:session request)))
  (POST "/taskadd" [:as request] (handle-task-add-post (:params request) (:session request)))

;;Groups
  (GET "/taskdesk/group/new" [:as request] (view/render-edit-group nil (:session request)))
  (GET "/taskdesk/group/delete/:id" [id] (.delete-item group-service id)
                                         (response/redirect "/taskdesk"))

  (POST "/groupadd" request (.add-item-over group-service request)
                            (response/redirect "/taskdesk"))

  (route/not-found "Page not found")
  (route/resources "/"))

(def engine
  (-> (handler/site app-routes)
      (middleware/wrap-json-body {:keywords? true})
      (ss/wrap-session)
      (wrap-cookies)))
