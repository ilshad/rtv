(ns user
  (:require [clojure.repl :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :as namespace-repl]
            [environ.core :refer [env]]
            [rtv.system :as system]))

(def sys nil)

(def conf (select-keys env [:datomic-uri :http-server-port :facebook]))

(defn go []
  (alter-var-root #'sys (constantly (system/system conf)))
  (alter-var-root #'sys component/start))

(defn stop []
  (alter-var-root #'sys (fn [s] (when s (component/stop s)))))

(defn reset []
  (stop)
  (namespace-repl/refresh :after 'user/go))

(defn test-request [uri & {:as opts}]
  (merge
   {:uri uri
    :request-method :get}
   opts))
