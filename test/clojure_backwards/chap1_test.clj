(ns clojure-backwards.chap1-test
  (:require [clojure.test :refer :all]
            [clojure-backwards.chap1 :refer :all]))

(deftest co2-estimate-test
  (testing "CO2"
    (is (= 470 (co2-estimate 2050)))))

(deftest meditate-test
  (testing "Meditate v1.0"
    (is (= "Hi" (meditate "hi" true))))

  (testing "Meditate v1.0"
    (is (= "HI!" (meditate "hi", false)))))

(deftest meditate-test-2
  (testing "Meditate v2.0: calmness 1"
    (is (= "WHAT WE DO NOW ECHOES IN ETERNITY, I TELL YA!" (meditate2 "what we do now echoes in eternity" 1))))

  (testing "Meditate v2.0: calmness 6"
    (is (= "What we do now echoes in eternity" (meditate2 "what we do now echoes in eternity" 6))))

  (testing "Meditate v2.0: calmness 10"
    (is (= "ytinrete ni seohce won od ew tahw" (meditate2 "what we do now echoes in eternity" 10))))

  (testing "Meditate v2.0: calmness 50"
    (is (= nil (meditate2 "what we do now echoes in eternity" 50)))))

(deftest meditate-test-3
  (testing "Meditate v3.0: calmness 1"
    (is (= "WHAT WE DO NOW ECHOES IN ETERNITY, I TELL YA!" (meditate3 "what we do now echoes in eternity" 1))))

  (testing "Meditate v3.0: calmness 6"
    (is (= "What we do now echoes in eternity" (meditate3 "what we do now echoes in eternity" 6))))

  (testing "Meditate v3.0: calmness 10"
    (is (= "ytinrete ni seohce won od ew tahw" (meditate3 "what we do now echoes in eternity" 10))))

  (testing "Meditate v3.0: calmness 50"
    (is (= nil (meditate3 "what we do now echoes in eternity" 50)))))