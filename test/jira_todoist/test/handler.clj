(ns jira-todoist.test.handler
  (:require [clojure.test :refer :all]
            [jira-todoist.handler :refer :all]
            [ring.mock.request :as mock]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World"))))
  
  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
