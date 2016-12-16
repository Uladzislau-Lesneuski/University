(ns taskdesk.views.view
  (:use hiccup.page
        hiccup.element)
  (:require [taskdesk.views.renderer :as renderer]))

(defn render-home-page
  [session]
  (renderer/render "home.html" {:docs "document" :session session}))

(defn render-signin-page
  []
  (renderer/render "auth.html"))

(defn render-user-page
  [user session]
  (renderer/render "user.html" {:user user :session session}))

(defn render-signup-page
  []
  (renderer/render "signup.html"))

(defn render-taskdesk-page
  [tasks groups user]
  (renderer/render "taskdesk.html" {:tasks tasks :groups groups :session user}))

(defn render-edit-task
  [task users groups stats session]
  (renderer/render "taskedit.html" {:task task :users users :groups groups :stats stats :session session}))

(defn render-edit-group
  [group session]
  (renderer/render "groupedit.html" {:group group :session session}))
