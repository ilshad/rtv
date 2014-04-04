(ns rtv.upload
  (:require [ring.util.response :as response]
            [clj-time.core :as time]
            [rtv.util :as util]))

(defn- string-to-sign [bucket-name s3-object-name s3-object-type expires]
  (let [amazon-headers "x-amz-acl:public-read"]
    (str "PUT\n\n" s3-object-type
         "\n" expires
         "\n" amazon-headers
         "\n/" bucket-name
         "/" s3-object-name)))

(defn sign-s3-put [{params :params conf :aws}]
  (let [object-name (:s3-object-name params)
        object-type (:s3-object-type params)
        bucket-name (:s3-bucket-name conf)
        secret-key (:s3-secret-key conf)
        s3-url (:s3-url conf)
        access-key (:s3-access-key conf)
        expires (-> 100 time/seconds time/from-now)
        raw (string-to-sign bucket-name object-name object-type expires)
        sig (util/b64-encode-string (util/hex-hmac-sha1 secret-key raw))
        signed-request (str s3-url "/" bucket-name "/" object-name
                            "?AWSAccessKeyId=" access-key
                            "&Expires=" expires
                            "&Signature" sig)
        url (str "http://s3.amazonaws.com/" bucket-name "/" object-type)]
    (response/response
     {:signed_request signed-request
      :url url})))
