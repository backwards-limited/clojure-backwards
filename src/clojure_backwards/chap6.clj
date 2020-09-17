(ns clojure-backwards.chap6
  (:require
    [clojure.pprint :as pp]
    [clojure-backwards.serena-williams :as sw]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [semantic-csv.core :as sc]
    [clojure.math.numeric-tower :as math]))

(def grocery-articles [{:name "Flour"
                        :weight 1000      ; grams
                        :max-dimension 140 ; millimeters
                        }
                       {:name "Bread"
                        :weight 350
                        :max-dimension 250}
                       {:name "Potatoes"
                        :weight 2500
                        :max-dimension 340}
                       {:name "Pepper"
                        :weight 85
                        :max-dimension 90}
                       {:name "Ice cream"
                        :weight 450
                        :max-dimension 200}
                       {:name "Green beans"
                        :weight 300
                        :max-dimension 120}
                       {:name "Olive oil"
                        :weight 400
                        :max-dimension 280}])

(defn article-stream [n]
  (repeatedly n #(rand-nth grocery-articles)))

(println (article-stream 12))

; A predicate so that we'll know when to stop filling a grocery bag
(defn full-bag? [items]
  (let [weight (apply + (map :weight items))
        size (apply + (map :max-dimension items))]
    (or (> weight 3200)
        (> size 800))))

(println (full-bag? (article-stream 10)))

(println (full-bag? (article-stream 1)))

(println (full-bag? '()))

; In the following we've defined an accumulator, this time with two fields:
; :bags will hold the list of all the completed bags and
; :current-bag will hold the items we are testing.
; When :current-bag fills up, we will place it in :bags and start off with a fresh, empty vector in :current-bag.

(defn bag-sequences* [{:keys [current-bag bags] :as acc} stream]
  (cond
    (not stream) (conj bags current-bag)

    (full-bag? (conj current-bag (first stream))) (recur (assoc acc
                                                      :current-bag [(first stream)]
                                                      :bags (conj bags current-bag)
                                                    ) (next stream))

    :otherwise-bag-not-full (recur (update acc
                                :current-bag conj (first stream)
                              ) (next stream))))

(defn bag-sequences [stream]
  (bag-sequences* {:bags []
                   :current-bag []} stream))

(println (bag-sequences (article-stream 12)))


; There is also "loop" e.g.
(def process identity)

(defn grocery-verification [input-items]
  (loop [remaining-items input-items
         processed-items []]

    (if (not (seq remaining-items))
      processed-items

      (recur (next remaining-items) (conj processed-items (process (first remaining-items)))))))


; Rewrite bag-sequences using loop

(defn looping-bag-sequences [stream]

  (loop [remaining-stream stream
         acc {:current-bag []
              :bags []}]

    (let [{:keys [current-bag bags]} acc]

      (cond
        (not remaining-stream) (conj bags current-bag)

        (full-bag? (conj current-bag (first remaining-stream))) (recur (next remaining-stream)
                                                                       (assoc acc
                                                                         :current-bag [(first remaining-stream)]
                                                                         :bags (conj bags current-bag)))

        :otherwise-bag-not-full (recur (next remaining-stream)
                                       (assoc acc :current-bag (conj current-bag (first remaining-stream))))))))


; Exercise
; Let's solve a complex problem: finding the most efficient path through a network of nodes.
; Or, to put it differently: how to travel cheaply between European capitals.
; We have is a list of city-to-city connections and an amount in euros.

(def routes
  [[:paris :london 236]
   [:paris :frankfurt 121]
   [:paris :milan 129]
   [:milan :rome 95]
   [:milan :barcelona 258]
   [:barcelona :madrid 141]
   [:madrid :lisbon 127]
   [:sevilla :lisbon 138]
   [:madrid :sevilla 76]
   [:barcelona :sevilla 203]
   [:madrid :paris 314]
   [:frankfurt :milan 204]
   [:frankfurt :berlin 170]
   [:frankfurt :geneva 180]
   [:geneva :paris 123]
   [:geneva :milan 85]
   [:frankfurt :prague 148]
   [:milan :vienna 79]
   [:vienna :prague 70]
   [:paris :amsterdam 139]
   [:amsterdam :berlin 176]
   [:amsterdam :frankfurt 140]
   [:vienna :bratislava 15]
   [:bratislava :prague 64]
   [:prague :warsaw 110]
   [:berlin :warsaw 52]
   [:vienna :budapest 43]
   [:prague :budapest 91]])

; The goal is to write a function that takes two cities and returns a list of cities that represents the best route.

; We'll build up a table (that can be queried) that looks like this:
{:paris {:london 236
         :frankfurt 121
         :milan 129
         ;;...etc...
         }}

(defn route-list->distance-map [route-list]
  (->> route-list
       (map (fn [[_ city cost]] [city cost]))
       (into {})))

(defn grouped-routes [routes]
  (->> routes
       (mapcat (fn [[origin-city dest-city cost :as r]]
                    [r [dest-city origin-city cost]]))

       (group-by first)

       (map (fn [[k v]] [k (route-list->distance-map v)]))

       (into {})))

(println (:paris (grouped-routes routes)))

(def lookup (grouped-routes routes))

(println (str "=> LOOKUP: " lookup))

(println (get-in lookup [:paris :madrid]))
; Can we go back to Paris?
(println (get-in lookup [:madrid :paris]))

(println (get-in lookup [:paris :bratislava]))

(defn find-path* [route-lookup destination path]
  (let [position (last path)]
    (cond
      (= position destination) path

      (get-in route-lookup [position destination]) (conj path destination)

      :otherwise-we-search (let [path-set (set path)
                                 from-here (remove path-set (keys (get route-lookup position)))]
                             (when-not (empty? from-here)
                               (->> from-here
                                    (map (fn [pos] (find-path* route-lookup destination (conj path pos))))
                                    (remove empty?)
                                    (mapcat (fn [x] (if (keyword? (first x)) [x] x)))))))))

(println (find-path* lookup :rome [:rome]))

(println (find-path* lookup :sevilla [:madrid]))

(def small-routes (grouped-routes [
                                   [:paris :milan 100]
                                   [:paris :geneva 100]
                                   [:geneva :rome 100]
                                   [:milan :rome 100]]))

(println small-routes)

(println (find-path* small-routes :rome [:paris]))


(defn cost-of-route [route-lookup route]
  (apply +
         (map (fn [start end] (get-in route-lookup [start end]))
              route (next route))))

(println (cost-of-route lookup [:london :paris :amsterdam :berlin :warsaw]))

(defn min-route [route-lookup routes]
  (reduce (fn [current-best route]
            (let [cost (cost-of-route route-lookup route)]
              (if (or (< cost (:cost current-best))
                      (= 0 (:cost current-best)))

                {:cost cost :best route}

                current-best)))

          {:cost 0 :best [(ffirst routes)]}

          routes))


(defn find-path [route-lookup origin destination]
  (min-route route-lookup (find-path* route-lookup destination [origin])))

(println (find-path lookup :paris :rome))