(ns rtv.util
  (:require [clojure.data.codec.base64 :as b64]))

(defn random-string [length]
  (let [ascii-codes (concat (range 48 58) (range 66 91) (range 97 123))]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))

(defn b64-encode-string [string]
  (String.
   (b64/encode (.getBytes string))
   "UTF-8"))

(defn hex-hmac-sha1 [key input]
  (let [secret (javax.crypto.spec.SecretKeySpec.
                (.getBytes key "UTF-8")
                "HmacSHA1")
        hmac-sha1 (doto (javax.crypto.Mac/getInstance "HmacSHA1")
                    (.init secret))
        bytes (. hmac-sha1 doFinal (. input getBytes "UTF-8"))]
    (apply str (map (partial format "%02x") bytes))))
