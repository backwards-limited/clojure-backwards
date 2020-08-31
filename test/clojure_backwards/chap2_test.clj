(ns clojure-backwards.chap2-test
  (:require [clojure.test :refer :all]
            [clojure-backwards.chap2 :refer :all]))

(deftest encode-letter-test
  (testing "Encode letter 'a'"
    (is (= "#10816" (encode-letter "a" 7)))))

(deftest decode-letter-test
  (testing "Decode letter from 2 words to get S"
    (is (= "S" (decode-letter "#7225" 2)))))

(deftest encode-and-decode-test
  (testing "Encode phrase and decode back"
    (let [phrase "If you want to keep a secret, you must also hide it from yourself"
          encoding (encode phrase)]
      (is (= phrase (decode encoding))))))

(deftest gemstone-test
  (testing "Ruby hardness"
    (is (= 9.0 (durability gemstone-db :ruby))))

  (testing "Moissanite hardness"
    (is (= 9.5 (durability gemstone-db :moissanite))))

  (testing "Change color"
    (let [updated-db (change-color gemstone-db :ruby "New Red")]
      (is (= "New Red" (get-in updated-db [:ruby :properties :color])))))

  (testing "Sell"
    (let [updated-db (sell gemstone-db :moissanite 123)]
      (is (= 44 (get-in updated-db [:moissanite :stock])))
      (is (= 123 (last (get-in updated-db [:moissanite :sales])))))))

(deftest im-memory-database-test
  (testing "Create table and drop"
    (write-db {})
    (let [db (create-table :clients)]
      (is (= db {:clients {:data [], :indexes {}}})))
    (let [db (drop-table :clients)]
      (is (= db {}))))
  (testing "Create table and insert"
    (create-table :fruits)
    (insert :fruits {:name "Apricot" :stock 30} :name)
    (insert :fruits {:name "Grapefruit" :stock 6} :name)
    (is (= 2 (count (get-in (read-db) [:fruits :data]))))))