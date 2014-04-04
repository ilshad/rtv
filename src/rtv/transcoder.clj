(ns rtv.transcoder
  (:require [clojure.core.async :refer [go <! >! chan]]
            [rtv.util :as util]
            [rtv.db :as db]))

(defn start-task [person-eid file-url]
  "/xyz")

(defn create-callback
  "Return tuple: callback URI and callback ring handler."
  [db root-uri-path]
  (let [uri (str root-uri-path "/" (util/random-string 7))
        callback (fn [req] nil)]
    [uri callback]))
