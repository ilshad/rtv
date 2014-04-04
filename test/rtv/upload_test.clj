(ns rtv.upload-test
  (:require [clojure.test :refer :all]
            [rtv.upload :refer :all]))


(deftest test-sign-s3
  (testing "Tesing ring handler: s3-put-sign"
    (let [params {:s3-object-name "test-object"
                  :s3-object-type "test-type"}
          conf {:s3-url "http://sample.com"
                :s3-bucket-name "test-bucket"
                :s3-access-key "test-access"
                :s3-secret-key "test-secret"}]
      (is (not= (s3-put-sign {:params params :aws conf})
                ""))))
  (testing "Testing ring handler: s3-put-done"
    (let [params {:result-url "http://foo.bar.io/yesno"}]
      )))
