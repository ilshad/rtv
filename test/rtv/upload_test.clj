(ns rtv.upload-test
  (:require [clojure.test :refer :all]
            [rtv.upload :refer :all]))


(deftest test-sign-s3-put
  (let [params {:s3-object-name "test-object"
                :s3-object-type "test-type"}
        conf {:s3-url "http://sample.com"
              :s3-bucket-name "test-bucket"
              :s3-access-key "test-access"
              :s3-secret-key "test-secret"}]
    (is (not= (sign-s3-put {:params params :aws conf})
              ""))))
