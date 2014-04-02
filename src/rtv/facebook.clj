(ns rtv.facebook
  (:require [ring.util.response :as response]
            [ring.util.request :as request]))

(defn- connect-request? [req]
  (and (= "/api/facebook/connect" (request/path-info req))
       (= :post (:request-method req))))

(defn- verify-token
  "Verify token and pull user attributes by servier-to-server Facebook
   API calls. Some caching, token exchange here."
  [client-id client-secret token user-id]
  {:email ""
   :first-name ""
   :last-name ""})

(defn workflow
  "Workflow for cemerick/friend implementing claims-based identity."
  [cfg]
  (fn [{:keys [params] :as req}]
    (if (connect-request? req)
      (if-let [user (verify-token (:client-id cfg)
                                  (:client-secret cfg)
                                  (params "access_token")
                                  (params "user_id"))]
        (let [cred-fn (-> req :cemerick.friend/auth-config :credential-fn)]
          (cred-fn (assoc user
                     :realm :realm/facebook
                     :user-id (params "user_id"))))))))
