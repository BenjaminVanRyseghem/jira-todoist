(ns jira-todoist.post
  (:require [clojure.data.json :as json]
            [jira-todoist.updatedTicket :as updated]))


(defmulti dispatchTicketEvent
  (fn [t] (get t :webhookEvent)))
(defmethod dispatchTicketEvent "jira:issue_updated"
  [ticket] (updated/dispatchOnUpdate ticket))



(defn handlePost [json]
  (def ticket (json/read-str json :key-fn keyword))
  (dispatchTicketEvent ticket))
