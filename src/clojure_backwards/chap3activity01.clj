(ns clojure-backwards.chap3activity01)

(require '[clojure.pprint :as pp])

(def walking-speed 4) ; km per hour

(def driving-speed 70) ; km per hour

(def paris {:lat 48.856483 :lon 2.352413})

(def bordeaux {:lat 44.834999 :lon -0.575490})

(defn distance
  [{from :from to :to}]
  (let [{lat1 :lat lon1 :lon} from
        {lat2 :lat lon2 :lon} to
        deglen 110.25
        x (- lat2 lat1)
        y (* (Math/cos lat2) (- lon2 lon1))]
    (* deglen (Math/sqrt (+ (* x x) (* y y))))))

(defmulti itinerary
  "Calculate the distance of travel between two locations,
  and the cost and duration based on the type of transport"
  :transport)

(defmethod itinerary :walking
  [journey]
  (let [distance (distance journey)
        duration (/ distance walking-speed)]
    {:cost 0 :distance distance :duration duration}))

(println (itinerary {:from paris :to bordeaux :transport :walking}))
; {:cost 0, :distance 491.61380776549225, :duration 122.90345194137306}

(def vehicle-cost-fns {
  :sporche (partial * 0.12 1.3)
  :tayato (partial * 0.07 1.3)
  :sleta (partial * 0.2 0.1)
})

; ([{:keys [camp armor] :or {armor 0} :as target} weapon]

(defmethod itinerary :driving
  [{:keys [vehicle] :as journey}]
  (let [distance (distance journey)
        duration (/ distance driving-speed)
        cost ((vehicle-cost-fns vehicle) distance)]
    {:cost cost :distance distance :duration duration}))

(println (itinerary {:from paris :to bordeaux :transport :driving :vehicle :tayato}))
; {:cost 44.7368565066598, :distance 491.61380776549225, :duration 7.023054396649889}