(ns jira-todoist.handler
  (:require
   [compojure.core :refer :all]
   [jira-todoist.post :as post]
   [compojure.handler :as handler]
   [compojure.route :as route]))

(defroutes app-routes
  (POST "/" {body :body} (println (post/handle-post (slurp body))))
  (route/not-found :IGNORED))

(def app
  (handler/site app-routes))
