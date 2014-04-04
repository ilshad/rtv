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

(defn sign-s3-put
  "Params: :s3-object-name, :s3-object-type, :s3-bucket-name.
   Conf: :s3-secret-key, :s3-url, :s3-access-key."
  [{params :params conf :aws}]
  (let [expires (-> 100 time/seconds time/from-now)
        raw (string-to-sign (:s3-bucket-name conf)
                            (:s3-object-name params)
                            (:s3-object-type params)
                            expires)
        sig (util/b64-encode-string
             (util/hex-hmac-sha1 (:s3-secret-key conf) raw))]
    (response/response
     {:signed_request (str (:s3-url conf) "/"
                           (:s3-bucket-name conf) "/"
                           (:s3-object-name params)
                           "?AWSAccessKeyId=" (:s3-access-key conf)
                           "&Expires=" expires
                           "&Signature" sig)
      :url (str "http://s3.amazonaws.com/"
                (:s3-bucket-name conf) "/"
                (:s3-object-type params))})))
