(ns jira-todoist.info
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]))


(letfn [ (read-info []
                    (json/read-str
                     (slurp (io/file (-> (java.io.File. "info.json") .getAbsolutePath)))
                     :key-fn keyword)) ]
  (defn todoist-project-name []
    (get (read-info) :todoistProject))

  (defn todoist-username []
    (get (read-info) :email))

  (defn todoist-jira-url []
    (get (read-info) :jiraUrl))

  (defn todoist-password []
    (get (read-info) :password)))
