(ns clojure-backwards.chap3exercise)

(require '[clojure.pprint :as pp])

(def player {:name "Lea" :health 200 :position {:x 10 :y 10 :facing :north}})

; move multimethod
; The dispatch function should determine the dispatch value by retrieving the :facing value in the :position map of a player entity.
; The :facing value could be one of the following values :north, :south, :west, :east

; Without composition
; (defmulti move #(:facing (:position %)))

; With composition
(defmulti move (comp :facing :position))

; So, we call "multi" with a player to move.
; multi applies its function to work out which "method" to dispatch this player to.
; It could be "north" - and indeed let's implement that one first - north receives the player that we called "multi" with.

(defmethod move :north
  [player]
  (update-in player [:position :y] inc))

; By moving a player north, its y value should be incremented
(println (move player))
; {:name Lea, :health 200, :position {:x 10, :y 11, :facing :north}}

(defmethod move :south
  [player]
  (update-in player [:position :y] dec))

(defmethod move :west
  [player]
  (update-in player [:position :x] dec))

(defmethod move :east
  [player]
  (update-in player [:position :x] inc))

(println (move {:position {:x 10 :y 10 :facing :west}}))

; For an undefined direction, don't move
(defmethod move :default
  [player]
  player)

(println (move {:position {:x 10 :y 10 :facing :wall}}))