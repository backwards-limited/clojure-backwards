(ns clojure-backwards.chap5activity1
  (:require
    [clojure.pprint :as pp]
    [clojure-backwards.serena-williams :as sw]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [semantic-csv.core :as sc]
    [clojure.math.numeric-tower :as math]))

; ELO

; A function implementing the formula for calculating the probability of a player defeating another player:
(defn match-probability
  [player-1-rating player-2-rating]
  (/ 1
     (+ 1
        (math/expt 10 (/ (- player-2-rating player-1-rating) 400)))))

(defn recalculate-rating [k-factor previous-rating expected-outcome real-outcome]
  (+ previous-rating (* k-factor (- real-outcome expected-outcome))))


; Player ratings:
; This is the most important part: a huge map linking each player to his rating.
; The map will be updated with the new ratings for the two players in the match being analyzed.

; Success count:
; For each match where one of the two players has a better than 50% chance of winning, did the expected winner actually win?
; By counting successes, you'll be able to divide by the total number of match predictions to determine the precision of your predictions.

; Total match count:
; The total number of matches that have been considered.

; Prediction count:
; The number of matches where a winner could be predicted – that is, matches where the forecast was not 50-50.
; Since we're excluding those matches from the success count, we need to exclude them from the prediction count.

;(defn blah [csv-path k-factor]
;  (with-open [csv (io/reader csv-path)]
;    (->> (csv/read-csv csv)
;         sc/mappify)))

(defn elo-world
  ([csv-path k]
   (with-open [csv (io/reader csv-path)]
     (->> (csv/read-csv csv)
          sc/mappify

          (sc/cast-with {:winner_sets_won sc/->int
                         :loser_sets_won sc/->int
                         :winner_games_won sc/->int
                         :loser_games_won sc/->int})

          (reduce (fn [{:keys [players] :as acc}
                       {:keys [:winner_name :winner_slug :loser_name :loser_slug] :as match}]
                    (let [winner-rating (get players winner_slug 400)
                          loser-rating (get players loser_slug 400)
                          winner-probability (match-probability winner-rating loser-rating)
                          loser-probability (- 1 winner-probability)
                          predictable-match? (not= winner-rating loser-rating)
                          prediction-correct? (> winner-rating loser-rating)
                          correct-predictions (if (and predictable-match? prediction-correct?)
                                                (inc (:correct-predictions acc))
                                                (:correct-predictions acc))
                          predictable-matches (if predictable-match?
                                                (inc (:predictable-match-count acc))
                                                (:predictable-match-count acc))]
                      (-> acc
                          (assoc :predictable-match-count predictable-matches)
                          (assoc :correct-predictions correct-predictions)
                          (assoc-in [:players winner_slug] (recalculate-rating k winner-rating winner-probability 1))
                          (assoc-in [:players loser_slug] (recalculate-rating k loser-rating loser-probability 0))
                          (update :match-count inc))))
                  {:players {}
                   :match-count 0
                   :predictable-match-count 0
                   :correct-predictions 0})

          ))))

(def ratings (elo-world "match_scores_1991-2016_unindexed_csv.csv" 32))

(println (get-in ratings [:players "rojer-federer"]))