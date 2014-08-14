(ns jira-todoist.info
   (:require
      [clojure.data.json :as json]
      [clojure.java.io :as io]))

(defn readInfo []
  (json/read-str (slurp (io/file (-> (java.io.File. "info.json") .getAbsolutePath))) :key-fn keyword))

(defn todoistProjectName []
  (get (readInfo) :todoistProject))

(defn todoistUsername []
  (get (readInfo) :email))

(defn todoistJiraUrl []
  (get (readInfo) :jiraUrl))

(defn todoistPassword []
  (get (readInfo) :password))

(defn todoistFixedStatus []
  (get (readInfo) :fixedStatus))
