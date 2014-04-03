(ns rtv.web
  (:require [compojure.route :as route]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET POST ANY]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.json :as ring-json]
            [ring.adapter.jetty :refer [run-jetty]]
            [com.stuartsierra.component :as c]
            [cemerick.friend :as friend]
            [rtv.facebook :as facebook]
            [rtv.auth :as auth]))

(defroutes routes
  (GET "/" req (str (-> req :db :uri)))
  (route/not-found "Page not found"))

(defn- wrap-components [handler db]
  (fn [req]
    (handler (assoc req :db db))))

(defn- make-handler [db facebook-cfg]
  (-> #'routes
      (wrap-components db)
      (friend/authenticate
       {:credential-fn (auth/credential-fn db)
        :workflows [(facebook/workflow facebook-cfg)]})
      site
      ring-json/wrap-json-params
      ring-json/wrap-json-response
      (wrap-resource "public")
      wrap-content-type))

(defrecord HTTPServer [port facebook-cfg db http-server]
  c/Lifecycle

  (start [this]
    (let [handler (make-handler db facebook-cfg)
          server (run-jetty handler {:port port :join? false})]
      (assoc this
        :handler handler
        :http-server server)))
  
  (stop [this]
    (.stop (:http-server this))))

(defn web-component [port facebook-cfg]
  (map->HTTPServer {:port port :facebook-cfg facebook-cfg}))
