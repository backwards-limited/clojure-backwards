(ns clojure-backwards.chap8activity1
  (:use
    [clojure.string :rename {replace str-replace, reverse str-reverse}]
    [clojure.pprint :only (print-table)]
    [clojure.set :exclude (join)]))

; 1) Import the clojure.string namespace with use and the :rename keyword for the replace and reverse functions.

; 2) Create a set of users:
(def users #{"mr_paul smith" "dr_john blake" "miss_katie hudson"})

; 3) Replace the underscore between the honorifics and the first names.
(def parsed-users (map #(str-replace % #"_" " ") users))

(println parsed-users)

; 4) Use the capitalize function to capitalize each person's initials in the user group.
(def caps-parsed-users (map #(capitalize %) parsed-users))

(println caps-parsed-users)

(println (split (str-replace "mr_paul smith" #"_" " ") #" "))

(def updated-users (into #{}
                         (map #(join " "
                                     (map (fn [sub-str] (capitalize sub-str))
                                          (split (str-replace % #"_" " ") #" ")))
                              users)))

(println updated-users)

; 6) Import only the print-table function from the clojure.pprint namespace.

; 7) Print a table with users:
(print-table (map #(hash-map :user-name %) updated-users))

; 8) Import the clojure.set namespace, excluding the join function.

; 9/10) Create a set of admins.
(def admins #{"Mr Paul Smith" "Miss Katie Hudson" "Dr Mike Rose" "Mrs Tracy Ford"})

; 11) Call the subset? function on two sets of users and admins.
(println (subset? users admins))