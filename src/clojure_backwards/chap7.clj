(ns clojure-backwards.chap7
  (:require
    [clojure.pprint :as pp]))

(defn our-range [n]
  (lazy-seq
    (cons n (our-range (inc n)))))

(println (take 5 (our-range 0)))


; Runners and hikers want to know how much of the time they are going uphill or downhill.
; The incoming data is a potentially endless sequence of tuples containing an elevation in meters,
; and a timestamp, the number of milliseconds since the user started their exercise.

(def sample-data [
  [24.2 420031]
  [25.8 492657]
  [25.9 589014]
  [23.8 691995]
  [24.7 734902]
  [23.2 794243]
  [23.1 836204]
  [23.5 884120]])

; A peak or a valley can be detected by comparing three consecutive items.
(defn local-max? [[a b c]]
  (and (< (first a) (first b))
       (< (first c) (first b))))

(defn local-min? [[a b c]]
  (and (> (first a) (first b))
       (> (first c) (first b))))

(println (local-max? (take 3 sample-data)))

(defn inflection-points [data]
  (lazy-seq
    (let [current-series (take 3 data)]
      (cond
        (< (count current-series) 3) '()

        (local-max? current-series) (cons
                                      (conj (second current-series) :peak)
                                      (inflection-points (rest data)))

        (local-min? current-series) (cons
                                      (conj (second current-series) :valley)
                                      (inflection-points (rest data)))

        :otherwise (inflection-points (rest data))))))

(println (inflection-points sample-data))

(println (take 15 (inflection-points (cycle sample-data))))


; Exercise
; Running average (of potatoes)
; (def endless-potatoes (repeatedly (fn [] (+ 10 (rand-int 390)))))
(def endless-potatoes (repeatedly #(+ 10 (rand-int 390))))

(println (take 5 endless-potatoes))

; We need a way of representing the current average for each item in the list.
; Beyond just the weight of the current potato, we need the potato count at that point in the sequence and the accumulated weight so far.
; We can use a three-item tuple to hold those three values:
; [200 5 784]
; the fifth potato in the list weighs 200 grams,
; and the total weight of the first five potatoes was 784

(defn average-potatoes [prev arrivals]
  (lazy-seq
    (if-not arrivals
      '()

      (let [[_ n total] prev
            current [(first arrivals)
                     (inc (or n 0))
                     (+ (first arrivals) (or total 0))]]

        (cons
          current

          (average-potatoes
            current
            (next arrivals)))))))

(println (take 3 (average-potatoes '() endless-potatoes)))