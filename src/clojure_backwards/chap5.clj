(ns clojure-backwards.chap5)

(require '[clojure.pprint :as pp])

(def weather-days [{
  :max 31
  :min 27
  :description :sunny
  :date "2019-09-24"
} {
  :max 28
  :min 25
  :description :cloudy
  :date "2019-09-25"
} {
  :max 22
  :min 18
  :description :rainy
  :date "2019-09-26"
} {
  :max 23
  :min 16
  :description :stormy
  :date "2019-09-27"
} {
  :max 35
  :min 19
  :description :sunny
  :date "2019-09-28"
}])

(println (apply max (map :max weather-days)))

(println
  (reduce (fn [max-day-so-far this-day]
            (if (> (:max max-day-so-far) (:max this-day))
              max-day-so-far
              this-day))
          weather-days))


; As we move through the sequence, the reducing function updates the :maximum and :minimum fields when new values are discovered.
(reduce (fn [{:keys [minimum maximum]} new-number]
          {:minimum (if (and minimum (> new-number minimum))
                      minimum
                      new-number)
           :maximum (if (and maximum (< new-number maximum))
                      maximum
                      new-number)})

        {} ;; Accumulator or initial value, in this case an empty Map

        [5 23 5004 845 22])

; Let's reduce over a vector where summation of segments should not exceed 20
(reduce (fn [{:keys [segments current] :as accum} n]
          (let [current-with-n (conj current n)
                total-with-n (apply + current-with-n)]

            (if (> total-with-n 20)
              (assoc accum
                :segments (conj segments current)
                :current [n])

              (assoc accum
                :current current-with-n))))

        {:segments [] :current []}

        [4 19 4 9 5 12 5 3 4 1 1 9 5 18])

; Resulting in:
; {:segments [[4] [19] [4 9 5] [12 5 3] [4 1 1 9 5]], :current [18]}

; Mmmm. But we left 18 behind - it should be in its own subsequence at the end of segments. Let's try again
(defn segment-by-sum [limit ns]
  (let [result (reduce (fn [{:keys [segments current] :as accum} n]
                         (let [current-with-n (conj current n)
                               total-with-n (apply + current-with-n)]

                           (if (> total-with-n limit)
                             (assoc accum
                               :segments (conj segments current)
                               :current [n])

                             (assoc accum
                               :current current-with-n))))

                       {:segments [] :current []}

                       ns)]

    (conj (:segments result) (:current result))))

(println (segment-by-sum 20 [4 19 4 9 5 12 5 3 4 1 1 9 5 18]))

; Resulting in:
; [[4] [19] [4 9 5] [12 5 3] [4 1 1 9 5] [18]]


; Let's "window" with reduce
(def numbers [4 9 2 3 7 9 5 2 6 1 4 6 2 3 3 6 1])

; For each integer in the list, we want to return a two-item tuple containing:
; - The integer itself
; - If the integer is odd, the sum of the consecutive odd integers preceding it; if it's even, the sum of the consecutive even integers

; Following this logic, the first 9 in the list should be replaced with [9 0], since it is preceded by an even integer.
; The second 9, on the other hand, should be replaced with [9 10], since it is preceded by a 3 and a 7.

(defn parity-totals [ns]
  (:ret
    (reduce (fn [{:keys [current] :as acc} n]
              (if (and (seq current)
                       (or (and (odd? (last current)) (odd? n))
                           (and (even? (last current)) (even? n))))

                (-> acc
                    (update :ret conj [n (apply + current)])
                    (update :current conj n))

                (-> acc
                    (update :ret conj [n 0])
                    (assoc :current [n]))))

            {:current [] :ret []}

            ns)))

(println (parity-totals numbers))


; Exercise - measuring elevation differences on slopes

; The data you have is a list of tuples: the first value is the distance from the start of the race, and the second is the elevation at that point.
(def distance-elevation
  [[0 400]
   [12.5 457]
   [19 622]
   [21.5 592]
   [29 615]
   [35.5 892]
   [39 1083]
   [43 1477]
   [48.5 1151]
   [52.5 999]
   [57.5 800]
   [62.5 730]
   [65 1045]
   [68.5 1390]
   [70.5 1433]
   [75 1211]
   [78.5 917]
   [82.5 744]
   [84 667]
   [88.5 860]
   [96 671]
   [99 584]
   [108 402]
   [115.5 473]])

; NOTE - If we are looking back, how do we know how far we are from the next peak or the next valley?
; Simple: we'll reverse the racecourse data so that when we're looking back, we're actually looking forward!

(defn distances-elevation-to-next-peak-or-valley
  [data]

  (->
    (reduce
      (fn [{:keys [current] :as acc} [distance elevation :as this-position]]

        )

      {:current []
       :calculated []}

      (reverse data))

    :calculated

    reverse))