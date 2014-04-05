(ns rtv.upload
  (:require [ring.util.response :refer [response redirect]]
            [cemerick.friend :as friend]
            [clj-time.core :as time]
            [rtv.transcoder :as transcoder]
            [rtv.util :as util]))

(defn- string-to-sign [bucket-name s3-object-name s3-object-type expires]
  (let [amazon-headers "x-amz-acl:public-read"]
    (str "PUT\n\n" s3-object-type
         "\n" expires
         "\n" amazon-headers
         "\n/" bucket-name
         "/" s3-object-name)))

(defn s3-put-sign
  "Request params: :s3_object_name, :s3_object_type.
   conf: :s3-bucket-name, :s3-url, :s3-secret-key, :s3-access-key."
  [{params :params conf :aws}]
  (let [expires (-> 100 time/seconds time/from-now)
        raw (string-to-sign (:s3-bucket-name conf)
                            (:s3_object_name params)
                            (:s3_object_type params)
                            expires)
        sig (util/b64-encode-string
             (util/hex-hmac-sha1 (:s3-secret-key conf) raw))]
    (response
     {:signed_request (str (:s3-url conf) "/"
                           (:s3-bucket-name conf) "/"
                           (:s3_object_name params)
                           "?AWSAccessKeyId=" (:s3-access-key conf)
                           "&Expires=" expires
                           "&Signature" sig)
      :url (str "http://s3.amazonaws.com/"
                (:s3-bucket-name conf) "/"
                (:s3_object_type params))})))

(defn s3-put-done
  "Request params: :result_url"
  [{params :params :as req}]
  (let [user (friend/current-authentication req)
        url (transcoder/start-task (:identity user) (:result_url params))]
    (redirect url)))
