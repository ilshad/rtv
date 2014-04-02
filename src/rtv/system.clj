(ns rtv.system
  (:require [com.stuartsierra.component :as c]
            [rtv.db :as db]
            [rtv.web :as web]))

(defn system [conf]
  (c/system-map
     :db (db/db-component (:datomic-uri conf))
     :web (c/using
           (web/web-component (:http-server-port conf)
                              (:facebook conf))
           [:db])))
