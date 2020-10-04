(ns clojurescript-testing.core
  (:require [cuerdas.core :as str]))

(defn adder [x y]
  (+ x y))

(defn profanity-filter [string]
  (if (str/includes? string "bad")
    (str/replace string "bad" "great")
    string))