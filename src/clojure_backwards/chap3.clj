(ns clojure-backwards.chap3)

(require '[clojure.pprint :as pp])

(defn print-records-rubbish [coords]
  (let [lat (first coords)
        lon (last coords)]
    (println (str "Latitude: " lat " - " "Longitude: " lon))))

(print-records-rubbish [48.9615 2.4372])

; What we are essentially doing when binding the first element to lat and the second to lon is destructuring:
; we are taking each element out of their sequential data structure.
; This use case is very common, and Clojure provides a built-in syntax for destructuring data structures to bind their values to symbols.

(defn print-records [coords]
  (let [[lat lon] coords]
    (println (str "Latitude: " lat " - " "Longitude: " lon))))

(print-records [48.9615 2.4372])

; Visually
(let
  ;;
  ;;   [1 2 3]
  ;;    | | |
      [[a b c] [1 2 3]] (println a b c))

; What about Map destructuring - well without
(defn print-map-rubbish-coords [airport]
  (let [lat (:lat airport)
        lon (:lon airport)
        name (:name airport)]
    (println (str name " is located at Latitude: " lat " - " "Longitude: " lon))))

(print-map-rubbish-coords {:lat 48.9615, :lon 2.4372, :code "LFPB", :name "Paris Le Bourget Airport"})

(defn print-map-coords [airport]
  (let [{lat :lat lon :lon airport-name :name} airport]
    (println (str airport-name " is located at Latitude: " lat " - " "Longitude: " lon))))

(print-map-coords {:lat 48.9615, :lon 2.4372, :code "LFPB", :name "Paris Le Bourget Airport"})

; and the shorter version when keys and symbols can have the same names
(defn print-map-shorter-coords [airport]
  (let [{:keys [lat lon name]} airport]
    (println (str name " is located at Latitude: " lat " - " "Longitude: " lon))))

(print-map-shorter-coords {:lat 48.9615, :lon 2.4372, :code "LFPB", :name "Paris Le Bourget Airport"})


; Flights example data to be destructured
[
  1425,
  "Bob Smith",
  "Allergic to unsalted peanuts only",
  [[48.9615, 2.4372], [37.742, -25.6976]],
  [[37.742, -25.6976], [48.9615, 2.4372]]
]

(def booking [
  1425,
  "Bob Smith",
  "Allergic to unsalted peanuts only",
  [[48.9615, 2.4372], [37.742, -25.6976]],
  [[37.742, -25.6976], [48.9615, 2.4372]]
])

(let [[id customer-name sensitive-info flight1 flight2 flight3] booking]
  (println id customer-name flight1 flight2 flight3))

(let [[_ customer-name _ flight1 flight2 flight3] booking]
  (println customer-name flight1 flight2 flight3))

(let [[_ customer-name _ & flights] booking]
  (println (str customer-name " booked " (count flights) " flights.")))

(defn print-flight [flight]
  (let [[departure arrival] flight
        [lat1 lon1] departure
        [lat2 lon2] arrival]
    (println (str "Flying from: Lat " lat1 " Lon " lon1 ", Flying to: Lat " lat2 " Lon " lon2))))

(print-flight [[48.9615 2.4372] [37.742 -25.6976]])

(defn print-flight-2
  [[[lat1 lon1] [lat2 lon2]]]
  (println (str "Flying from: Lat " lat1 " Lon " lon1 ", Flying to: Lat " lat2 " Lon " lon2)))

(print-flight-2 [[48.9615 2.4372] [37.742 -25.6976]])

; Final version for our flights
(defn print-booking [booking]
  (let [[_ customer-name _ & flights] booking]
    (println (str customer-name " booked " (count flights) " flights."))

    (let [[flight1 flight2 flight3] flights]
      (when flight1 (print-flight flight1))
      (when flight2 (print-flight flight2))
      (when flight3 (print-flight flight3)))))

(print-booking booking)


; Another example of (flight) data destructuring

{
  :id 8773
  :customer-name "Alice Smith"
  :catering-notes "Vegetarian on Sundays"
  :flights [{
    :from {:lat 48.9615 :lon 2.4372 :name "Paris Le Bourget Airport"},
    :to {:lat 37.742 :lon -25.6976 :name "Ponta Delgada Airport"}
  }, {
    :from {:lat 37.742 :lon -25.6976 :name "Ponta Delgada Airport"},
    :to {:lat 48.9615 :lon 2.4372 :name "Paris Le Bourget Airport"}
  }]
}

(def mapjet-booking {
  :id 8773
  :customer-name "Alice Smith"
  :catering-notes "Vegetarian on Sundays"
  :flights [{
    :from {:lat 48.9615 :lon 2.4372 :name "Paris Le Bourget Airport"},
    :to {:lat 37.742 :lon -25.6976 :name "Ponta Delgada Airport"}
  }, {
    :from {:lat 37.742 :lon -25.6976 :name "Ponta Delgada Airport"},
    :to {:lat 48.9615 :lon 2.4372 :name "Paris Le Bourget Airport"}
  }]
})

(let [{:keys [customer-name flights]} mapjet-booking
      flight-count (count flights)]
  (println (str customer-name " booked " flight-count (if (= 1 flight-count) " flight" " flights"))))

(defn print-mapjet-flight [flight]
  (let [{{lat1 :lat lon1 :lon} :from
         {lat2 :lat lon2 :lon} :to} flight]
    (println (str "Flying from: Lat " lat1 " Lon " lon1 ", Flying to: Lat " lat2 " Lon " lon2))))

(print-mapjet-flight (first (:flights mapjet-booking)))

(defn print-mapjet-flight-2
  [{{lat1 :lat lon1 :lon} :from {lat2 :lat lon2 :lon} :to}]
  (println (str "Flying from: Lat " lat1 " Lon " lon1 ", Flying to: Lat " lat2 " Lon " lon2)))

(print-mapjet-flight-2 (first (:flights mapjet-booking)))

(defn print-mapjet-booking [booking]
  (let [{:keys [customer-name flights]} booking
        flight-count (count flights)]
    (println (str customer-name " booked " flight-count (if (= 1 flight-count) " flight" " flights")))

    (let [[flight1 flight2 flight3] flights]
      (when flight1 (print-mapjet-flight flight1))
      (when flight2 (print-mapjet-flight flight2))
      (when flight3 (print-mapjet-flight flight3)))))

(print-mapjet-booking mapjet-booking)


; Function arity

(def weapon-damage {:fists 10.0 :staff 35.0 :sword 100.0 :cast-iron-saucepan 150.0})

(defn strike-org
  ([enemy] (strike-org enemy :fists))
  ([enemy weapon]
   (let [damage (weapon weapon-damage)]
     (update enemy :health - damage))))

(println (strike-org {:hame "noob" :health 100}))

(println (strike-org {:hame "noob" :health 100} :sword))

(println (strike-org {:hame "noob" :health 100} :cast-iron-saucepan))


; Variadic functions

(defn welcome
  [player & friends]
  (println (str "Welcome to the Parenthmazes " player "!"))
  (when (seq friends)
    (println (str "Sending " (count friends) " friend request(s) to the following players: " (clojure.string/join ", " friends)))))

(welcome "Jon")

(welcome "Jon" "Arya" "Tyrion" "Petyr")

(defn welcome2
  ([player]
   (println (str "Welcome to Parentmazes (single-player mode), " player "!")))

  ([player & friends]
   (println (str "Welcome to Parenthmazes (multi-player mode), " player "!"))
   (println (str "Sending " (count friends) " friend request(s) to the following players: " (clojure.string/join ", " friends)))))


; New version of strike

; The target entity can contain an :armor key, which contains a coefficient used to calculate the final amount of damage.
; The bigger the number, the better the armor.
; For example, an armor value of 0.8 for a strike of 100 points results in 20 damage points being inflicted.
; An armor value of 0.1 results in 90 damage points being inflicted,
; 0 means no armor,
; and 1 means invincible.

(defn strike
  ([target weapon]
   (let [points (weapon weapon-damage)]
     (if (= :gnomes (:camp target))
       (update target :health + points)

       (let [armor (or (:armor target) 0)
             damage (* points (- 1 armor))]
         (update target :health - damage))))))

(def enemy {:name "Zulkaz" :health 250 :camp :trolls})

(println (strike enemy :sword))

(def ally {:name "Carla" :health 80 :camp :gnomes})

(println (strike ally :staff))

(def armored-enemy {:name "Zulkaz" :health 250 :armor 0.8 :camp :trolls})

(println (strike armored-enemy :sword))

; Now we would like to use our associative destructuring technique to retrieve the camp and armor values directly
; from the function parameters, and reduce the amount of code in the function's body.
; The only problem we have is that we still need to return an updated version of the target entity,
; but how could we both destructure the target entity and keep a reference of the target parameter?
; Clojure has your back – you can use the special key :as to bind the destructured map to a specific name.

(defn strike
  "With one argument, strike a target with a default :fists `weapon`.
   With two argument, strike a target with `weapon`.
   Strike will heal a target that belongs to the gnomes camp."
  ([target] (strike target :fists))
  ([{:keys [camp armor] :or {armor 0} :as target} weapon]
   (let [points (weapon weapon-damage)]
     (if (= :gnomes camp)
       (update target :health + points)
       (let [damage (* points (- 1 (or armor)))]
         (update target :health - damage))))))


; More indepth using "partial functions", "function composition" and "dispatch tables".

; Note:
; We want our sword weapon to simply subtract 100 points, but there is an issue with partial:
; ((partial - 100) 150) gives -50
; because the function call is equivalent to (- 100 150) and not what we want which is (- 150 100)
; So instead of a partial function we resort to an anonymous function

(def weapon-fn-map {
  :fists (fn [health] (if (< health 100) (- health 10) health))
  :staff (partial + 35)
  :sword #(- % 100)
  :cast-iron-saucepan #(- % 100 (rand-int 50))
  :sweet-potato identity
})

(println ((weapon-fn-map :fists) 150))

(println ((weapon-fn-map :fists) 50))

(println ((weapon-fn-map :staff)))

(println ((weapon-fn-map :staff) 150))

(println ((weapon-fn-map :sword) 150))

(println ((weapon-fn-map :cast-iron-saucepan) 200))

(defn strike2
  "With one argument, strike a target with a default :fists `weapon`.
  With two arguments, strike a target with `weapon` and return the target entity"
  ([target]
   (strike2 target :fists))
  ([target weapon]
   (let [weapon-fn (weapon weapon-fn-map)]
     (update target :health weapon-fn))))

(def arnold {:name "Arnold" :health 250})

(println (strike2 arnold :sweet-potato))

(println (strike2 arnold :cast-iron-saucepan))

; If we want to strike with more than one weapon, we could do the following, but next we'll improve by using composition
(println (strike2 (strike2 arnold :sword) :staff))

(println (update arnold :health (comp (:sword weapon-fn-map) (:fists weapon-fn-map))))

; We can strike with all weapons.
; Remember, to pass each element of a collection as a parameter of a function, we can use apply.

(defn mighty-strike
  "Strike a `target` with all weapons"
  [target]
  (let [weapon-fn (apply comp (vals weapon-fn-map))]
    (update target :health weapon-fn)))

(println (mighty-strike arnold))

; Let's now strike using multimethods and pass in the weapon HashMap

(defmulti strike (fn [m] (get m :weapon)))

(defmulti strike :weapon)

; Handle differently for when health is below 50 by striking all the way down to 0
; We need to uncomment the following and comment out the 2 above
;(defmulti strike
;  (fn [{{:keys [:health]} :target weapon :weapon}]
;    (if (< health 50) :finisher weapon)))

(defmethod strike :finisher [_] 0)

(defmethod strike :sword
  [{{:keys [:health]} :target}]
  (- health 100))

(defmethod strike :cast-iron-saucepan
  [{{:keys [:health]} :target}]
  (- health 100 (rand-int 50)))

(println (strike {:weapon :sword :target {:health 200}}))

(println (strike {:weapon :cast-iron-saucepan :target {:health 200}}))

(defmethod strike :default
  [{{:keys [:health]} :target}]
  health)

(println (strike {:weapon :spoon :target {:health 200}}))

; Call with health 50 and above
(println (strike {:weapon :sword :target {:health 200}}))

; Call with health below 50 - Comment out the above "strike" functions and we should get 0
(println (strike {:weapon :spoon :target {:health 30}}))