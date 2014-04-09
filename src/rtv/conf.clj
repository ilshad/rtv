(ns rtv.conf
  (:require [ring.util.response :refer [response]]))

(defn handler [req]
  (response
   {:facebook-app-id (-> req :facebook :client-id)}))
