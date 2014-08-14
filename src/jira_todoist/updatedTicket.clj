(ns jira-todoist.updatedTicket
  (:require [jira-todoist.todoist :as todoist]))

(def interestingFields ["assignee" "description" "summary" "status"])

(defn statusChanged [ticket]
  (let [ status (get-in ticket [:issue :fields :status :name]) ]
    (if (= status "Resolved")
      (todoist/completeItem ticket)
      (if (= status "In Progress")
             (todoist/setItemToToday ticket)
             "Uninteresting status")
    )))

(defn dispatchField [ticket]
    (let [
          items (get-in ticket [:changelog :items])
          assignee (some (fn [item] (= (get item :field) "assignee")) items)
          status (some (fn [item] (= (get item :field) "status")) items)
          ]

      (if assignee
        (todoist/createNewItem ticket)
        (if status
          (statusChanged ticket)
          (todoist/updateItemContent ticket))
      )))


(defn ticketValid? [ticket]
  (def items (get-in ticket [:changelog :items]))
  (def valid (some (fn [item] (contains? (set interestingFields) (get item :field))) items))
  valid)

(defn dispatchOnUpdate [ticket]
  "Dispatched on updated"
  (if (ticketValid? ticket)
    (dispatchField ticket)
    "Uninteresting update"))
