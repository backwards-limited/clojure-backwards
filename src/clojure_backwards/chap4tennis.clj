(ns clojure-backwards.chap4tennis
  (:require
    [clojure.pprint :as pp]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [semantic-csv.core :as sc]))

; Function that returns first row of data
(defn first-match [csv]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         first)))

(println (first-match "match_scores_1991-2016_unindexed_csv.csv"))

; Now only select interested fields from first five rows
(defn five-matches [csv]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         (map #(select-keys % [:tourney_year_id :winner_name :loser_name :winner_sets_won :loser_sets_won]))
         (take 5)
         doall)))

(println (five-matches "match_scores_1991-2016_unindexed_csv.csv"))

(defn federer-wins [csv]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         (filter #(= "Roger Federer" (:winner_name %)))
         (map #(select-keys % [:winner_name
                               :loser_name
                               :winner_sets_won
                               :loser_sets_won
                               :winner_games_won
                               :loser_games_won
                               :tourney_year_id
                               :tourney_slug]))
         doall)))

(println (take 3 (federer-wins "match_scores_1991-2016_unindexed_csv.csv")))

(defn match-query [csv pred]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         sc/mappify
         (filter pred)
         (map #(select-keys % [:winner_name
                               :loser_name
                               :winner_sets_won
                               :loser_sets_won
                               :winner_games_won
                               :loser_games_won
                               :tourney_year_id
                               :tourney_slug]))
         doall)))

; Predicate searching for all Federer's matches, wins and loses
#(or (= "Roger Federer" (:winner_name %))
     (= "Roger Federer" (:loser_name %)))

; We could also use a "set" as a predicate
#((hash-set (:winner_name %) (:loser_name %)) "Roger Federer")
; First, we define a set that includes the :winner_name and :loser_name fields and then we ask: is Roger Federer a member of that set?
; Note, we've written hash-set here instead of using the literal notation, #{…}, to avoid confusion with the #(…) of the anonymous function.

(def federer #((hash-set (:winner_name %) (:loser_name %)) "Roger Federer"))

(println (count (match-query "match_scores_1991-2016_unindexed_csv.csv" federer)))


; Federer vs Nadal

; Predicate for all matches between these 2 players.

; First attempt
#(and
   (or (= (:winner_name %) "Roger Federer")
       (= (:winner_name %) "Rafael Nadal"))
   (or (= (:loser_name %)  "Roger Federer")
       (= (:loser_name %)  "Rafael Nadal")))

; Second attempt
#(= (hash-set (:winner_name %) (:loser_name %))
    #{"Roger Federer" "Rafael Nadal"})
; We don't care about the order, or which one is the winner or loser.

(println (take 3 (match-query "match_scores_1991-2016_unindexed_csv.csv"
                              #(= (hash-set (:winner_name %) (:loser_name %))
                                  #{"Roger Federer" "Rafael Nadal"}))))