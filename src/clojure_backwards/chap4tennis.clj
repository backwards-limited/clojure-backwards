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

; To use the :winner_sets_won and :loser_sets_won fields in a calculation of some kind, we need to cast them as integers first.
; Use the cast-with function of semantic-csv:
(defn five-matches-int-sets [csv]
  (with-open [r (io/reader csv)]
    (->> (csv/read-csv r)
         (map #(select-keys % [:tourney_year_id :winner_name :loser_name :winner_sets_won :loser_sets_won]))
         #(update % :winner_sets_won cast Integer)
         #(update % :loser_sets_won cast Integer))))

(println (five-matches-int-sets "match_scores_1991-2016_unindexed_csv.csv"))
