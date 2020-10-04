(ns clojurescript-testing.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [clojurescript-testing.core-test]))

(doo-tests 'clojurescript-testing.core-test)