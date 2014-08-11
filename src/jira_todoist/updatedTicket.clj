(ns jira-todoist.updatedTicket)

(defn ticketValid? [ticket]
  (def changes (get ticket :changelog))
  (def items (get changes :items))
  (def valid (some (fn [item] (= (get item :field) "assignee")) items))
  valid)

(defn dispatchOnUpdate [ticket]
  "Dispatched on updated"
  (if (ticketValid? ticket) :VALID)
)
