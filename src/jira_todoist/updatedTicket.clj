(ns jira-todoist.updatedTicket
  (:require [jira-todoist.todoist :as todoist]))

(def interestingFields ["assignee" "description" "summary"])

(defn dispatchField [item ticket]
    (case (get item :field)
      ("assignee") (todoist/assigneeChanged ticket)
      ("description") (todoist/descriptionChanged ticket)
      ("summary") (todoist/summaryChanged ticket)
      :IGNORED
      ))


(defn ticketValid? [ticket]
  (def items (get-in ticket [:changelog :items]))
  (println (get (first items) :field))
  (def valid (some (fn [item] (contains? (set interestingFields) (get item :field))) items))
  valid)

(defn dispatchOnUpdate [ticket]
  "Dispatched on updated"
  (if (ticketValid? ticket)
    (map (fn [item] (dispatchField item ticket)) (get-in ticket [:changelog :items]))
     :USELESS
     ))
