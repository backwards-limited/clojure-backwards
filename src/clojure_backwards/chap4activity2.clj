(ns clojure-backwards.chap4activity2
  (:require
    [clojure.pprint :as pp]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [semantic-csv.core :as sc]))

; Using the tennis dataset, write a function that provides information about a tennis rivalry e.g.
; (defn rivalry-data [csv player-1 player-2])

; resulting in the map e.g.
; {:first-victory-player-1 []
;  :first-victory-player-2 []
;  :total-matches []
;  :total-victories-player-1 int
;  :total-victories-player-2 int
;  :most-competitive-matches []
; }

;(defn rivalry-data [csv player-1 player-2]
;  (with-open [r (io/reader csv)]
;    (->> (csv/read-csv r)
;         (filter (let [matches #(= (hash-set (:winner_name %) (:loser_name %))
;                           #{player-1 player-2})]
;           matches))
;         (map #(select-keys % [:winner_name
;                               :loser_name
;                               :winner_sets_won
;                               :loser_sets_won
;                               :winner_games_won
;                               :loser_games_won
;                               :tourney_year_id
;                               :tourney_slug]))
;         doall)))

(defn rivalry-data [csv player-1 player-2]
  (with-open [r (io/reader csv)]
    (let [rivalry-seq (->> (csv/read-csv r)
                           sc/mappify
                           (sc/cast-with {:winner_sets_won sc/->int
                                          :loser_sets_won sc/->int
                                          :winner_games_won sc/->int
                                          :loser_games_won sc/->int})
                           (filter #(= (hash-set (:winner_name %) (:loser_name %))
                                       #{player-1 player-2}))
                           (map #(select-keys % [:winner_name
                                                 :loser_name
                                                 :winner_sets_won
                                                 :loser_sets_won
                                                 :winner_games_won
                                                 :loser_games_won
                                                 :tourney_year_id
                                                 :tourney_slug])))
          player-1-victories (filter #(= (:winner_name %) player-1) rivalry-seq)
          player-2-victories (filter #(= (:winner_name %) player-2) rivalry-seq)]
      {
       :first-victory-player-1 (first player-1-victories)
       :first-victory-player-2 (first player-2-victories)
       :total-matches (count rivalry-seq)
       :total-victories-player-1 (count player-1-victories)
       :total-victories-player-2 (count player-2-victories)
       :most-competitive-matches (->> rivalry-seq
                                      (filter #(= 1 (- (:winner_sets_won %) (:loser_sets_won %)))))
       })))

(println (rivalry-data "match_scores_1991-2016_unindexed_csv.csv" "Roger Federer" "Rafael Nadal"))