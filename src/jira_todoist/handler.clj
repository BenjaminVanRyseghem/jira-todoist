(ns jira-todoist.handler
(:require
            [compojure.core :refer :all]
            [jira-todoist.post :as post]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] (println "webhook GET"))
  (POST "/" {body :body} (println (post/handlePost (slurp body))))
  (PUT "/" [] (println "webhook PUT"))
  (DELETE "/" [] (println "webhook DELETE"))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
