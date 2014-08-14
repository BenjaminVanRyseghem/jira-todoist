(ns jira-todoist.todoist
   (:require [jira-todoist.info :as info]
             [ring.util.codec :as codec]
             [clj-http.client :as client]
             [clojure.data.json :as json]))

(defn find-first
    [f coll]
    (first (filter f coll)))

(defn ^String substring?
  "True if s contains the substring."
  [substring ^String s]
  (.contains s substring))


(defn login []
  (let [
    options (codec/form-encode {:email (info/todoistUsername) :password (info/todoistPassword)})
    url (str "https://api.todoist.com/API/login?" options)
    json (get (client/get url) :body)
    body (json/read-str json :key-fn keyword)]
    (get body :token)))

(def getToken (memoize login))

(defn getProjects []
  (let [
    options (codec/form-encode {:token (getToken)})
    url (str "https://api.todoist.com/API/getProjects?" options)
    json (get (client/get url) :body)
    body (json/read-str json :key-fn keyword)]
    body))


(defn getProjectByName [name]
  (let [ projects (getProjects)
         my-filter (fn [each] (= (get each :name) name ))]
    (find-first my-filter projects)))


(def todoistProject (getProjectByName (info/todoistProjectName)))


(defn getItemsForProject [project]
  (let [
    options (codec/form-encode {:project_id (get project :id) :token (getToken)})
    url (str "https://api.todoist.com/API/getUncompletedItems?" options)
    json (get (client/get url) :body)
    body (json/read-str json :key-fn keyword)]
    body))

(defn findItemByName [project name]
  (let [ items (getItemsForProject project)
         my-filter (fn [each] (substring? name (get each :content)))]
    (find-first my-filter items)))

(defn findItemForTicket [project ticket]
  (findItemByName project (get-in ticket [:issue :key])))


(defn buildUrlForTicket [ticket]
  (str
   (info/todoistJiraUrl) "/browse/" (get-in ticket [:issue :key])
  ))

(defn createNewItemFromTicket [project ticket]
 (let [existingTicket (findItemForTicket todoistProject ticket)
       options (codec/form-encode {:content (str
                                             (get-in ticket [:issue :key])
                                             ": "
                                             (get-in ticket [:issue :fields :summary]))
                                   :priority 1
                                   :note (str
                                          (get-in ticket [:issue :fields :description])
                                          "\n\n==============================\nSee at: "
                                          (buildUrlForTicket ticket))
                                   :project_id (get project :id)
                                   :token (getToken)})
       url (str "https://api.todoist.com/API/addItem?" options)]

       (if (= existingTicket nil)
         (do
           (client/get url)
           "Ticket created")
         "Ticket already exists"
         )))

(defn updateItemFromTicket [project ticket]
   (let [existingTicket (findItemForTicket todoistProject ticket)
         options (codec/form-encode {:content (str
                                                  (get-in ticket [:issue :key])
                                                  ": "
                                                  (get-in ticket [:issue :fields :summary]))
                                     :id (get existingTicket :id)
                                     :token (getToken)})
         url (str "https://api.todoist.com/API/updateItem?" options)]
         (client/get url)
         "Ticket updated"))


(defn completeItemFromTicket [project ticket]
   (let [existingTicket (findItemForTicket todoistProject ticket)
         ids  [ (get existingTicket :id) ]
         options (codec/form-encode {:ids  [ ids ]
                                     :token (getToken)})
         url (str "https://api.todoist.com/API/completeItems?" options)]
         (client/get url)
         "Ticket completed"
     ))


(defn setItemToTodayFromTicket [project ticket]
   (let [existingTicket (findItemForTicket todoistProject ticket)
         options (codec/form-encode {:date_string "today"
                                     :id (get existingTicket :id)
                                     :token (getToken)})
         url (str "https://api.todoist.com/API/updateItem?" options)]
         (client/get url)
         "Ticket updated"))

;;
;; Tickets API
;;

(defn createNewItem [ticket]
 (createNewItemFromTicket todoistProject ticket))

(defn updateItemContent [ticket]
 (updateItemFromTicket todoistProject ticket))

(defn completeItem [ticket]
 (completeItemFromTicket todoistProject ticket))

(defn setItemToToday [ticket]
 (setItemToTodayFromTicket todoistProject ticket))

