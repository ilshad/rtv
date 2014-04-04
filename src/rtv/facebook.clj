(ns rtv.facebook
  (:require [ring.util.request :as request]
            [clj-http.lite.client :as http]
            [cheshire.core :as cheshire]))

(defn- connect-request? [req]
  (and (= "/api/facebook/connect" (request/path-info req))
       (= :post (:request-method req))))

(def fb-fields "first_name,last_name,picture,email,link,locale")

(defn fb-user [user-id token]
  (some->
   (http/get (str "https://graph.facebook.com/" user-id)
             {:query-params {:access_token token :fields fb-fields}})
   :body
   cheshire/parse-string))

(defn verify-token
  "Verify token and pull user attributes by server-to-server Facebook
   API calls. Some caching, token exchange here."
  [user-id token]
  (let [user (fb-user user-id token)]    
    {:email (user "email")
     :first-name (user "first_name")
     :last-name (user "last_name")
     :userpic (get-in user ["picture" "data" "url"])}))

(defn workflow
  "Workflow for cemerick/friend implementing claims-based identity."
  [{params :params :as req}]
  (if (connect-request? req)
    (if-let [user (verify-token (:user_id params) (:access_token params))]
      (let [cred-fn (-> req :cemerick.friend/auth-config :credential-fn)]
        (cred-fn (assoc user
                   :realm :login.realm/facebook
                   :user-id (:user_id params)))))))
