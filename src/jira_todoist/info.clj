(ns jira-todoist.info
   (:require
      [clojure.data.json :as json]
      [clojure.java.io :as io]))

(defn readInfo []
  (let [json (slurp (io/resource "info.json"))]
    (json/read-str json :key-fn keyword)
    ))

(defn todoistUsername [arg]
  (json/read-str (slurp arg)))

(readInfo)
(type readInfo)
(get readInfo :name )
