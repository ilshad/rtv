(ns rtv.frontend-config
  (:require [ring.util.response :as response]))

(defn handler [req]
  (response/response
   {:facebook-app-id (-> req :facebook :client-id)
    }))
