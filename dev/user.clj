(ns user
  (:require [clojure.repl :refer :all]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as c]
            [clojure.tools.namespace.repl :as namespace-repl]
            [environ.core :refer [env]]
            [datomic.api :as d]
            [rtv.system :as system]
            [rtv.db :as db]))

(def sys nil)

(def conf (select-keys env [:datomic-uri :http-server-port
                            :facebook :aws]))

(defn seed []
  (-> "test-data.edn" io/resource slurp read-string))

(defn go []
  (alter-var-root #'sys (constantly (system/system conf)))
  (alter-var-root #'sys c/start)
  @(d/transact (-> sys :db :conn) (seed)))

(defn stop []
  (alter-var-root #'sys (fn [s] (when s (c/stop s)))))

(defn reset []
  (stop)
  (namespace-repl/refresh :after 'user/go))

(defn test-request [uri & {:as opts}]
  (merge
   {:uri uri
    :request-method :get}
   opts))
