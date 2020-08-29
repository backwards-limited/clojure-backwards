(ns clojure-backwards.chap1)

(def base-co2 382)

(def base-year 2006)

(defn co2-estimate
  "Taking an int representing the `year` resulting in estimate of CO2 ppm"
  [year]
  (let [year-diff (- year base-year)]
    (+ base-co2 (* year-diff 2))))

(println (co2-estimate 2050))

(defn meditate
  "Meditation given `s` and `calm`"
  [s calm]
  (println "Clojure Meditate v1.0")
  (if calm
    (clojure.string/capitalize s)
    (str (clojure.string/upper-case s) "!")))

(defn meditate2
  "Better meditation given `s` and `calmness-level` of <= 5, 6-9, 10"
  [s calmness-level]
  (println "Clojure Meditate v2.0")
  (if (<= calmness-level 4)
    (str (clojure.string/upper-case s) ", I TELL YA!")
    (if (<= calmness-level 9)
      (clojure.string/capitalize s)
      (if (= calmness-level 10)
        (clojure.string/reverse s)))))

(defn meditate3
  "Return a transformed version of the string 's' based on the 'calmness-level'"
  [s calmness-level]
  (println "Clojure Meditate v3.0")
  (cond
    (<= calmness-level 4)   (str (clojure.string/upper-case s) ", I TELL YA!")
    (<= 5 calmness-level 9) (clojure.string/capitalize s)
    (= 10 calmness-level)   (clojure.string/reverse s)))