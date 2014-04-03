(ns rtv.db
  (:require [datomic.api :as d]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as c]))

(defn- schema []
  (-> "schema.edn" io/resource slurp read-string))

(defrecord DB [uri conn]
  c/Lifecycle

  (start [this]
    (let [created (d/create-database uri)
          conn (d/connect uri)]
      (when created
        @(d/transact conn (schema)))
      (assoc this
        :uri uri
        :conn conn)))

  (stop [this]
    (d/release conn)
    (assoc this :conn nil)))

(defn db-component [uri]
  (map->DB {:uri uri}))

;; Utility functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn tx-id
  "Resolve transaction and get real id instead of temporary id"
  [tx-result temp-id]
  (let [{:keys [db-after tempids]} tx-result]
    (d/resolve-tempid db-after tempids temp-id)))

(defn tx-entity
  "Resolve transaction and get newly created entity"
  [tx-result temp-id]
  (let [{:keys [db-after tempids]} tx-result]
    (d/entity db-after (d/resolve-tempid db-after tempids temp-id))))

;; DB component behavior
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Naming convention: names with star (*) take database value
;; instead of database component.

(defn login-eid*
  "Find login entity ID"
  [dbv realm user-id]
  (ffirst
   (d/q '[:find ?c
          :in $ ?realm ?user-id
          :where
          [?c :login/realm ?realm]
          [?c :login/user-id ?user-id]]
        dbv realm user-id)))

(defn login->person
  "Find person entity by login params"
  [db realm user-id]
  (let [dbv (d/db (:conn db))]
    (some->> (login-eid* dbv realm user-id)
             (d/q '[:find ?c
                    :in $ ?login-eid
                    :where
                    [?c :person/login ?login-eid]]
                  dbv)
             ffirst
             (d/entity dbv))))

(defn update-person
  "Update person's attribute _if necessary_. Takes person entity,
   attribute name and attribute value"
  [db person attr-name attr-value]
  (when (not= attr-value (get person attr-name))
    @(d/transact
      (:conn db)
      [[:db/add (:db/id person) attr-name attr-value]])))

(defn add-login [db realm user-id]
  (let [id (d/tempid :db.part/user)]
    (-> @(d/transact (:conn db) [[:db/add id :login/realm realm]
                                 [:db/add id :login/user-id user-id]])
        (tx-id id))))

(defn add-person [db email first-name last-name roles login-entity-id]
  (let [id (d/tempid :db.part/user)]
    (-> @(d/transact (:conn db) [{:db/id id
                                  :person/email email
                                  :person/first-name first-name
                                  :person/last-name last-name
                                  :person/role roles
                                  :person/login login-entity-id}])
        (tx-entity id))))

;; These functions are used in debug only
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn all-users [db]
  (d/q '[:find ?c
         :in $
         :where
         [?c :person/email]]
       (d/db (:conn db))))
