(ns rtv.video
  (:require [clojure.edn :as edn]
            [datomic.api :as d]
            [ring.util.response :refer [response]]))

(defn- person [e]
  {:title (str (:person/first-name e) " " (:person/last-name e))
   :videos-count (count (:video/_owner e))
   :followers-count (count (:person/_following e))
   :id (:db/id e)
   :userpic "/foo.jpg"})

(defn- tricks [es]
  (->>
   (map (fn [e] {:title (:trick/title e) :start (:trick/start e)})
        es)
   (sort-by :start)
   vec))

(defn info
  "Request params: id"
  [req]
  (let [dbv (d/db (-> req :db :conn))
        eid (edn/read-string (-> req :params :id))
        e (d/entity dbv eid)]
    (response
     {:uri (:video/uri e)
      :created (str (:video/created e))
      :title (:video/title e)
      :tags (vec (:video/tag e))
      :likes-count (count (:video/like e))
      :sport (-> e :video/sport :sport/title)
      :owner (person (:video/owner e))
      :tricks (tricks (:video/trick e))})))

