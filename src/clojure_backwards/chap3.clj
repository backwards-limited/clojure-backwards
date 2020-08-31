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

(def weapon-damage {:fists 10 :staff 35 :sword 100 :cast-iron-saucepan 150})

(defn strike
  ([enemy] (strike enemy :fists))
  ([enemy weapon]
   (let [damage (weapon weapon-damage)]
     (update enemy :health - damage))))

(println (strike {:hame "noob" :health 100}))

(println (strike {:hame "noob" :health 100} :sword))

(println (strike {:hame "noob" :health 100} :cast-iron-saucepan))