(ns clojure-backwards.chap4)

(require '[clojure.pprint :as pp])

(def x (map (fn [i] (* i 10)) [1 2 3 4 5]))

(println x)

(println (map count ["Let's" "measure" "word" "length" "now"]))

(println (map (fn [w] (str w ": " (count w))) ["Let's" "measure" "word" "length" "now"]))

(println (odd? 5))

(println (filter odd? [1 2 3 4 5]))

(def students [
  {:name "Eliza" :year 1994}
  {:name "Salma" :year 1995}
  {:name "Jodie" :year 1997}
  {:name "Kaitlyn" :year 2000}
  {:name "Alice" :year 2001}
  {:name "Pippa" :year 2002}
  {:name "Fleur" :year 2002}
])

(def before2000 #(< (:year %) 2000))

(println (take-while before2000 students))
(println (drop-while before2000 students))

; We can observe laziness in action by doing something that we never want to do in production code: introduce a side effect.

(defn our-range [limit]
  (take-while #(< % limit) (iterate inc 0)))

(println (our-range 5))

(println (map #(* 10 %) (our-range 5)))

(println (map (fn [i] (print ".") (* i 10)) (our-range 5)))

(def by-ten (map (fn [i] (print ".") (* i 10)) (our-range 5)))

(range)

(println
  (->> (range)
       (map #(* 10 %))
       (take 5)))

(def our-randoms (repeatedly #(rand-int 100)))

(println (take 5 our-randoms))

(defn some-random-integers [size]
  (take size (repeatedly (partial rand-int 100))))

(println (some-random-integers 12))


; Imagine that the game requires us to get the current points of all the players,
; maybe to calculate an average or to find the maximum and minimum values.
(def game-users
  [{:id 9342
    :username "speedy"
    :current-points 45
    :remaining-lives 2
    :experience-level 5
    :status :active}
   {:id 9854
    :username "stealthy"
    :current-points 1201
    :remaining-lives 1
    :experience-level 8
    :status :speed-boost}
   {:id 3014
    :username "sneaky"
    :current-points 725
    :remaining-lives 7
    :experience-level 3
    :status :active}
   {:id 2051
    :username "forgetful"
    :current-points 89
    :remaining-lives 4
    :experience-level 5
    :status :imprisoned}
   {:id 1032
    :username "wandering"
    :current-points 2043
    :remaining-lives 12
    :experience-level 7
    :status :speed-boost}
   {:id 7213
    :username "slowish"
    :current-points 143
    :remaining-lives 0
    :experience-level 1
    :status :speed-boost}
   {:id 5633
    :username "smarter"
    :current-points 99
    :remaining-lives 4
    :experience-level 4
    :status :terminated}
   {:id 3954
    :username "crafty"
    :current-points 21
    :remaining-lives 2
    :experience-level 8
    :status :active}
   {:id 7213
    :username "smarty"
    :current-points 290
    :remaining-lives 5
    :experience-level 12
    :status :terminated}
   {:id 3002
    :username "clever"
    :current-points 681
    :remaining-lives 1
    :experience-level 8
    :status :active}])

; The following 3 are equivalent

(println (map (fn [player] (:current-points player)) game-users))

(println (map #(:current-points %) game-users))

(println (map :current-points game-users))

; First attempt to filter out statuses
(def keep-statuses #{:active :imprisoned :speed-boost})

(filter
  (fn [player] (keep-statuses (:status player)))
  game-users)

; Here we do 2 things:
; get the field, and then test it.

; In this case, we could also use the comp function, which takes two functions and returns a new function,
; which is the result of calling the first function on the result of the second function.

; Second attempt
(filter
  (comp keep-statuses :status)
  game-users)

; Third attempt, where we will also combine only getting points for each filtered player:
(->> game-users
     (filter (comp keep-statuses :status))
     (map :current-points))


(def animal-names ["turtle" "horse" "cat" "frog" "hawk" "worm"])

; First attempt at removing mammals:
(println (remove
  (fn [animal-name]
    (or (= animal-name "horse")
        (= animal-name "cat")))
  animal-names))

; Second attempt
(println (remove #{"horse" "cat"} animal-names))


; Normalise a string - trim both ends and lower-case

; First attempt:
(require '[clojure.string :as string])

(defn normalize [s] (string/trim (string/lower-case s)))

(println (normalize "     PhOOy"))

; Second attempt:
(def normalize (comp string/trim string/lower-case))

(println (normalize "     PhOOy"))


; Weather
; We want to determine whether each day was warmer, colder, or the same as the previous day.
; This information could then be used to add up or down arrows to a visualization.

(def temperature-by-day [18 23 24 23 27 24 22 21 21 20 32 33 30 29 35 28 25 24 28 29 30])

(def trend
  (map (fn [today yesterday]
       (cond (> today yesterday) :warmer
             (< today yesterday) :colder
             (= today yesterday) :unchanged))
     (rest temperature-by-day) temperature-by-day))

(println trend)


; Average weather temperature
(def average-temp
  (let [total (apply + temperature-by-day)
        c (count temperature-by-day)]
    (/ total c)))

(println average-temp)


; Game of players where a player can be compared to another

(defn value-by-status [f field status users]
  (->> users
       (filter #(= (:status %) status))
       (map field)
       (apply f 0)))

(defn max-value-by-status [field status users]
  (value-by-status max field status users))

(defn min-value-by-status [field status users]
  (value-by-status min field status users))

(println "------ Game Stats ------")
(println (max-value-by-status :experience-level :imprisoned game-users))
(println (min-value-by-status :experience-level :imprisoned game-users))
(println (max-value-by-status :experience-level :terminated game-users))
(println (min-value-by-status :experience-level :terminated game-users))
(println (max-value-by-status :remaining-lives :active game-users))
(println (min-value-by-status :remaining-lives :active game-users))
(println (max-value-by-status :current-points :speed-boost game-users))