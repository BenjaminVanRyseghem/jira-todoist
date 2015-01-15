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


(letfn [(login []
               (let [options (codec/form-encode {:email (info/todoist-username) :password (info/todoist-password)})
                     url (str "https://api.todoist.com/API/login?" options)
                     json (get (client/get url) :body)
                     body (json/read-str json :key-fn keyword)]
                 (get body :token)))]

  (def get-token (memoize login)))

(defn get-projects []
  (let [options (codec/form-encode {:token (get-token)})
        url (str "https://api.todoist.com/API/getProjects?" options)
        json (get (client/get url) :body)
        body (json/read-str json :key-fn keyword)]
    body))


(defn get-project-by-name [name]
  (let [projects (get-projects)
        my-filter (fn [each] (= (get each :name) name))]
    (find-first my-filter projects)))


(def todoist-project (get-project-by-name (info/todoist-project-name)))


(defn get-items-for-project [project]
  (let [options (codec/form-encode {:project_id (get project :id) :token (get-token)})
        url (str "https://api.todoist.com/API/getUncompletedItems?" options)
        json (get (client/get url) :body)
        body (json/read-str json :key-fn keyword)]
    body))

(defn find-item-by-name [project name]
  (let [items (get-items-for-project project)
        my-filter (fn [each] (substring? name (get each :content)))]
    (find-first my-filter items)))

(defn find-item-for-ticket [project ticket]
  (find-item-by-name project (get-in ticket [:issue :key])))


(defn build-url-for-ticket [ticket]
  (str
   (info/todoist-jira-url) "/browse/" (get-in ticket [:issue :key])))

(defn create-new-item-from-ticket [project ticket]
  (let [existing-ticket (find-item-for-ticket todoist-project ticket)
        options (codec/form-encode {:content (str
                                              (get-in ticket [:issue :key])
                                              ": "
                                              (get-in ticket [:issue :fields :summary]))
                                    :priority 1
                                    :note (str
                                           (get-in ticket [:issue :fields :description])
                                           "\n\n==============================\nSee at: "
                                           (build-url-for-ticket ticket))
                                    :project_id (get project :id)
                                    :token (get-token)})
        url (str "https://api.todoist.com/API/addItem?" options)]

    (if (= existing-ticket nil)
      (do
        (client/get url)
        "Ticket created")
      "Ticket already exists")))

(defn update-item-from-ticket [project ticket]
  (let [existing-ticket (find-item-for-ticket todoist-project ticket)
        options (codec/form-encode {:content (str
                                              (get-in ticket [:issue :key])
                                              ": "
                                              (get-in ticket [:issue :fields :summary]))
                                    :id (get existing-ticket :id)
                                    :token (get-token)})
        url (str "https://api.todoist.com/API/updateItem?" options)]
    (client/get url)
    "Ticket updated"))


(defn complete-item-from-ticket [project ticket]
  (let [existing-ticket (find-item-for-ticket todoist-project ticket)
        ids [(get existing-ticket :id)]
        options (codec/form-encode {:ids  [ids]
                                    :token (get-token)})
        url (str "https://api.todoist.com/API/completeItems?" options)]
    (client/get url)
    "Ticket completed"))


(defn set-item-to-today-from-ticket [project ticket]
  (let [existing-ticket (find-item-for-ticket todoist-project ticket)
        options (codec/form-encode {:date_string "today"
                                    :id (get existing-ticket :id)
                                    :token (get-token)})
        url (str "https://api.todoist.com/API/updateItem?" options)]
    (client/get url)
    "Ticket updated"))

;;
;; Tickets API
;;

(defn create-new-item [ticket]
  (create-new-item-from-ticket todoist-project ticket))

(defn update-item-content [ticket]
  (update-item-from-ticket todoist-project ticket))

(defn complete-item [ticket]
  (complete-item-from-ticket todoist-project ticket))

(defn set-item-to-today [ticket]
  (set-item-to-today-from-ticket todoist-project ticket))
