(ns jira-todoist.post
  (:require [clojure.data.json :as json]
            [jira-todoist.updated-ticket :as updated]))


(defmulti dispatch-ticket-event
  (fn [t] (get t :webhookEvent)))

(defmethod dispatch-ticket-event "jira:issue_updated"
  [ticket] (updated/dispatch-on-update ticket))

(defn handle-post [json]
  (def ticket (json/read-str json :key-fn keyword))
  (dispatch-ticket-event ticket))
