(ns rtv.web
  (:require [compojure.route :as route]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET POST PUT DELETE]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.json :as ring-json]
            [ring.adapter.jetty :refer [run-jetty]]
            [com.stuartsierra.component :as c]
            [cemerick.friend :as friend]
            [rtv.facebook :as facebook]
            [rtv.upload :as upload]
            [rtv.video :as video]
            [rtv.auth :as auth]
            [rtv.conf :as conf]))

(defroutes routes
  (GET "/" req (str (-> req :db :uri)))
  (GET "/api/front/conf" req (conf/handler req))
  (GET "/api/front/s3-put-sign" req (upload/s3-put-sign req))
  (GET "/api/front/s3-put-done" req (upload/s3-put-done req))
  (GET "/api/front/video/:id" req (video/info req))
  (PUT "/api/front/video/:id/tricks/:start" req (video/put-trick req))
  (DELETE "/api/front/video/:id/tricks/:start" req (video/delete-trick req))
  (route/not-found "Page not found"))

(defn- wrap-components [handler db facebook aws]
  (fn [req]
    (handler (assoc req
               :db db
               :facebook facebook
               :aws aws))))

(defn- make-handler [db facebook-cfg aws-cfg]
  (-> #'routes
      (wrap-components db facebook-cfg aws-cfg)
      (friend/authenticate
       {:credential-fn (auth/credential-fn db)
        :workflows [facebook/workflow]})
      site
      ring-json/wrap-json-params
      ring-json/wrap-json-response
      (wrap-resource "public")
      wrap-content-type))

(defrecord HTTPServer [port facebook-cfg aws-cfg db http-server]
  c/Lifecycle

  (start [this]
    (let [handler (make-handler db facebook-cfg aws-cfg)
          server (run-jetty handler {:port port :join? false})]
      (assoc this
        :handler handler
        :http-server server)))
  
  (stop [this]
    (.stop (:http-server this))))

(defn web-component [port facebook-cfg aws-cfg]
  (map->HTTPServer {:port port
                    :facebook-cfg facebook-cfg
                    :aws-cfg aws-cfg}))
