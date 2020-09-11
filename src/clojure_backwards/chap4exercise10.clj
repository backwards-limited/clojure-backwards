(ns clojure-backwards.chap4exercise10)

(require
  '[clojure.pprint :as pp]
  '[clojure.data.csv :as csv]
  '[clojure.java.io :as io])

(def csv-file-name "match_scores_1991-2016_unindexed_csv.csv")

(println (with-open [r (io/reader csv-file-name)]
  (first (csv/read-csv r))))

(println (with-open [r (io/reader csv-file-name)]
           (count (csv/read-csv r))))

; Get the winner names from the first 5 matches - from the csv headers, the winner name is at index 7
(println (with-open [r (io/reader csv-file-name)]
  (->> (csv/read-csv r)
       (map #(nth % 7))
       (take 6)
       doall))) ; Because of laziness once println triggers resolution, the stream has already been closed, so we force resolution prior i.e. we force any effects