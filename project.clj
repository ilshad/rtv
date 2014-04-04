(defproject rtv "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                 [com.datomic/datomic-pro "0.9.4572"]
                 [com.stuartsierra/component "0.2.1"]
                 [com.draines/postal "1.11.1"]
                 [compojure "1.1.6"]
                 [environ "0.4.0"]
                 [ring "1.2.2"]
                 [hiccup "1.0.5"]
                 [enlive "1.1.5"]
                 [cheshire "5.3.1"]
                 [com.cemerick/friend "0.2.0"]
                 [ring/ring-json "0.2.0"]
                 [clj-time "0.6.0"]
                 [clj-http-lite "0.2.0"]
                 [org.clojure/data.codec "0.1.0"]]
  :plugins [[lein-environ "0.4.0"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]]
                   :env {:datomic-uri "datomic:mem://rtv"
                         :http-server-port 7777
                         :facebook {:client-id "123"
                                    :client-secret "xxx"}
                         :aws {:s3-url "http://xxx.com"
                               :s3-bucket-name "foo"
                               :s3-access-key "bar"
                               :s3-secret-key "baz"}}}})
