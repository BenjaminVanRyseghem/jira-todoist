(ns jira-todoist.info
   (:require
      [clojure.data.json :as json]
      [clojure.java.io :as io]))

(defn readInfo []
  (json/read-str (slurp (io/resource "info.json")) :key-fn keyword))

(defn todoistProjectName []
  (get (readInfo) :todoist-project))

(defn todoistUsername []
  (get (readInfo) :email))

(defn todoistPassword []
  (get (readInfo) :password))
