(ns rtv.video
  (:require [clojure.edn :as edn]
            [datomic.api :as d]
            [ring.util.response :refer [response created]]
            [rtv.db :as db]))

(defn- person [e]
  {:title (str (:person/first-name e) " " (:person/last-name e))
   :videos-count (count (:video/_owner e))
   :followers-count (count (:person/_following e))
   :id (:db/id e)
   :userpic "/foo.jpg"})

(defn- tricks [es]
  (->> (map (fn [e] {:title (:trick/title e)
                     :start (:trick/start e)})
            es)
       (sort-by :start)
       vec))

(defn info
  "Request params: id"
  [{{id :id} :params {conn :conn} :db}]
  (let [dbv (d/db conn)
        e (d/entity dbv (edn/read-string id))]
    (response
     {:uri (:video/uri e)
      :created (str (:video/created e))
      :title (:video/title e)
      :tags (vec (:video/tag e))
      :likes-count (count (:video/like e))
      :sport (-> e :video/sport :sport/title)
      :owner (person (:video/owner e))
      :tricks (tricks (:video/trick e))})))

(defn put-trick
  "Request params: id (video's id), title, start"
  [{{:keys [id start title]} :params db :db}]
  (db/add-trick db
                (edn/read-string id)
                title
                (edn/read-string start))
  (created (str "/api/front/video/" id "/tricks/" start)))

(defn delete-trick
  "Request params: id (video's id), start"
  [{{:keys [id start]} :params db :db}]
  (db/del-trick db
                (edn/read-string id)
                (edn/read-string start))
  {:status 204
   :headers {}
   :body ""})
