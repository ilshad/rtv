(ns rtv.transcoder
  (:require [rtv.db :as db]
            [rtv.util :as util]))

(defn create-callback
  "Return tuple: callback URI and callback ring handler."
  [db root-uri-path]
  (let [uri (str root-uri-path "/" (util/random-string 7))
        callback (fn [req] nil)]
    [uri callback]))
