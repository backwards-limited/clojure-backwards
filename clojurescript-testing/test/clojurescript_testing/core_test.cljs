(ns clojurescript-testing.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer-macros [for-all]]
            [clojure.test.check.clojure-test :refer-macros [defspec]]
            [cuerdas.core :as str]
            [clojurescript-testing.core :refer [profanity-filter]]))

(deftest profanity-filter-test
  (testing "Filter bad words"
    (is (= "Clojure is great" (profanity-filter "Clojure is bad"))))

  (testing "Filter does not replace good words"
    (are [string result]
      (= result (profanity-filter string))
      "Clojure is great" "Clojure is great"
      "Clojure is brilliant" "Clojure is brilliant")))