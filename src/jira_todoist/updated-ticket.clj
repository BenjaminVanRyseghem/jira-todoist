(ns jira-todoist.updated-ticket
  (:require [jira-todoist.todoist :as todoist]))

(def interesting-fields ["assignee" "description" "summary" "status"])

(defn status-changed [ticket]
  (let [ status (get-in ticket [:issue :fields :status :name]) ]
    (if (= status "Resolved")
      (todoist/complete-item ticket)
      (if (= status "In Progress")
        (todoist/set-item-to-today ticket)
        "Uninteresting status")
      )))

(defn dispatch-field [ticket]
  (let [
        items (get-in ticket [:changelog :items])
        assignee (some (fn [item] (= (get item :field) "assignee")) items)
        status (some (fn [item] (= (get item :field) "status")) items)
        ]

    (if assignee
      (todoist/create-new-item ticket)
      (if status
        (status-changed ticket)
        (todoist/update-item-content ticket))
      )))

(defn ticket-valid? [ticket]
  (def items (get-in ticket [:changelog :items]))
  (def valid (some (fn [item] (contains? (set interesting-fields) (get item :field))) items))
  valid)

(defn dispatch-on-update [ticket]
  "Dispatched on updated"
  (if (ticket-valid? ticket)
    (dispatch-field ticket)
    "Uninteresting update"))
