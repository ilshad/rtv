(ns rtv.auth
  (:require [rtv.db :as db]))

(defn- find-or-create-person [creds db]
  (let [{:keys [realm user-id email first-name last-name]} creds]
    (if-let [person (db/login->person db realm user-id)]
      ;; login exists
      ;; TODO may be fuck do not need this
      (let [update (partial db/update-person db person)]
        (update :person/email email)
        (update :person/first-name first-name)
        (update :person/last-name last-name)
        person)
      ;; new login, new person
      (let [login-eid (db/add-login db :realm/facebook user-id)]
        (db/add-person db email first-name last-name login-eid)))))

(defn- authentication-map [person]
  {:identity (:db/id person)
   :roles (:person/role person)})

(defn credential-fn [db]
  (fn [creds]
    (some-> creds
            (find-or-create-person db)
            authentication-map)))
