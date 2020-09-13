(ns clojure-backwards.chap5)

(require '[clojure.pprint :as pp])

(require '[clojure-backwards.serena-williams :as sw])

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

; There is more destructuring for easy access to the distance and elevation values inside the incoming tuple.
; The entire call to reduce is wrapped inside a -> threading macro.
; This is, of course, equivalent to (reverse (:calculated (reduce…)))
; but has the advantage of organizing the code according to how the data flows through the function.

; We will need to know whether the new position is on the same slope as the positions stored in current.
; Are we still going up, or down, or have we gone over a peak, or across the lowest part of a valley?
(defn same-slope-as-current? [current elevation]
  (or (= 1 (count current))
      (let [[[_ next-to-last] [_ the-last]] (take-last 2 current)]
        (or (>= next-to-last the-last elevation)
            (<= next-to-last the-last elevation)))))

; When we have at least two items, we can do some destructuring.
; This destructuring is doubly nested:
; first, we take the last two elements in current, using the take-last function,
; and then we extract and name the second part of those tuples.

; Now we have three values and we want to see whether they are either all increasing or all decreasing.

(println (same-slope-as-current? [[1 5] [2 10]] 15))

(println (same-slope-as-current? [[1 5] [2 10]] 5))

(println (same-slope-as-current? [[1 5]] 10))

(println (same-slope-as-current? [[1 5] [2 10] [3 15]] 20))

(fn [{:keys [current] :as acc} [distance elevation :as this-position]]
  (cond (empty? current)
        {:current [this-position]
         :calculated [{:race-position distance
                       :elevation elevation
                       :distance-to-next 0
                       :elevation-to-next 0}]}

        (same-slope-as-current? current elevation)
        (-> acc
            (update :current conj this-position)
            (update :calculated
                    conj
                    {:race-position distance
                     :elevation elevation
                     :distance-to-next (- (first (first current)) distance)
                     :elevation-to-next (- (second (first current)) elevation)}))

        :otherwise-slope-change
        (let [[prev-distance prev-elevation :as peak-or-valley] (last current)]
          (-> acc
              (assoc :current [peak-or-valley this-position])
              (update :calculated
                      conj
                      {:race-position distance
                       :elevation elevation
                       :distance-to-next (- prev-distance distance)
                       :elevation-to-next (- prev-elevation elevation)})))))

; Placing the above function in distances-elevation-to-next-peak-or-valley, we now have:
(defn distances-elevation-to-next-peak-or-valley
  [data]
  (->
    (reduce
      (fn [{:keys [current] :as acc} [distance elevation :as this-position]]
        (cond (empty? current)
              {:current [this-position]
               :calculated [{:race-position distance
                             :elevation elevation
                             :distance-to-next 0
                             :elevation-to-next 0}]}
              (same-slope-as-current? current elevation)
              (-> acc
                  (update :current conj this-position)
                  (update :calculated
                          conj
                          {:race-position distance
                           :elevation elevation
                           :distance-to-next (- (first (first current)) distance)
                           :elevation-to-next (- (second (first current)) elevation)}))
              :otherwise-slope-change
              (let [[prev-distance prev-elevation :as peak-or-valley] (last current)]
                (-> acc
                    (assoc :current [peak-or-valley this-position])
                    (update :calculated
                            conj
                            {:race-position distance
                             :elevation elevation
                             :distance-to-next (- prev-distance distance)
                             :elevation-to-next (- prev-elevation elevation)})))))
      {:current []
       :calculated []}
      (reverse data))
    :calculated
    reverse))

(println (distances-elevation-to-next-peak-or-valley distance-elevation))


; Exercide - Games of Serena Williams
(defn streak-string [current-wins current-losses]
  (cond (pos? current-wins) (str "Won " current-wins)
        (pos? current-losses) (str "Lost " current-losses)
        :otherwise "First match of the year"))

(defn serena-williams-win-loss-streaks [matches]
  (:matches
    (reduce (fn [{:keys [current-wins current-losses] :as acc}
                 {:keys [winner-name] :as match}]

              (let [this-match (assoc match :current-streak (streak-string current-wins current-losses))
                    serena-victory? (= winner-name "Williams S.")]
                (-> acc
                    (update :matches #(conj % this-match))

                    (assoc :current-wins (if serena-victory?
                                           (inc current-wins)
                                           0))

                    (assoc :current-losses (if serena-victory?
                                             0
                                             (inc current-losses))))))

            {:matches []
             :current-wins 0
             :current-losses 0}

            matches)))

(println (serena-williams-win-loss-streaks sw/serena-williams-2015))


; We have a list of some of the matches that Petra Kvitova played in 2014.
; Let's suppose you need to be able to quickly access the matches by date.
; You need to build a map where the keys are dates and the values are the individual matches.
(def matches
  [{:winner-name "Kvitova P.",
    :loser-name "Ostapenko J.",
    :tournament "US Open",
    :location "New York",
    :date "2016-08-29"}
   {:winner-name "Kvitova P.",
    :loser-name "Buyukakcay C.",
    :tournament "US Open",
    :location "New York",
    :date "2016-08-31"}
   {:winner-name "Kvitova P.",
    :loser-name "Svitolina E.",
    :tournament "US Open",
    :location "New York",
    :date "2016-09-02"}
   {:winner-name "Kerber A.",
    :loser-name "Kvitova P.",
    :tournament "US Open",
    :location "New York",
    :date "2016-09-05"}
   {:winner-name "Kvitova P.",
    :loser-name "Brengle M.",
    :tournament "Toray Pan Pacific Open",
    :location "Tokyo",
    :date "2016-09-20"}
   {:winner-name "Puig M.",
    :loser-name "Kvitova P.",
    :tournament "Toray Pan Pacific Open",
    :location "Tokyo",
    :date "2016-09-21"}])

(map :date matches)
; Results in:
; ("2016-08-29" "2016-08-31" ...

(def matches-by-date (zipmap (map :date matches) matches))

; Use the above Map to look up a match by date:
(println (get matches-by-date "2016-09-20"))

(def dishes [{
  :name "Carrot Cake"
  :course :dessert
} {
  :name "French Fries"
  :course :main
} {
  :name "Celery"
  :course :appetizer
} {
  :name "Salmon"
  :course :main
} {
  :name "Rice"
  :course :main
} {
  :name "Ice Cream"
  :course :dessert
}])