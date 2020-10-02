# Lazy Sequences

```clojure
(defn iterate-range [] (iterate inc 0))
=> #'clojure-backwards.chap3/iterate-range

(take 5 (iterate-range))
=> (0 1 2 3 4)

; Or how about
(def iterate-range (iterate inc 0))
=> #'clojure-backwards.chap3/iterate-range

(take 5 iterate-range)
=> (0 1 2 3 4)
```

At a lower level we have the **lazy-seq** macro:

```clojure
(defn our-range [n]
  (lazy-seq
    (cons n (our-range (inc n)))))

(take 5 (our-range 0))
=> (0 1 2 3 4)
```

Without **lazy-seq**, this recursive call would execute immediately, and continuously, until the stack blew up. With **lazy-seq**, the next call does not happen; instead, a reference to that future call is returned.

The future, unrealized calculation is often called a **thunk**.

## Exercise

Runners and hikers want to know how much of the time they are going uphill or downhill. The incoming data is a potentially endless sequence of tuples containing an elevation in meters, and a timestamp, the number of milliseconds since the user started their exercise.

Each tuple looks as follows:

```clojure
[24.2 420031]
```

```clojure
(def sample-data [
  [24.2 420031]
  [25.8 492657]
  [25.9 589014]
  [23.8 691995]
  [24.7 734902]
  [23.2 794243]
  [23.1 836204]
  [23.5 884120]])
```

A peak or a valley can be detected by comparing three consecutive items.

```clojure
(defn local-max? [[a b c]]
  (and (< (first a) (first b))
       (< (first c) (first b))))

(defn local-min? [[a b c]]
  (and (> (first a) (first b))
       (> (first c) (first b))))
```

Or a bit more concise:

```clojure
(defn local-max? [[[a _] [b _] [c _]]]
  (and (< a b) (< c b)))

(defn local-min? [[[a _] [b _] [c _]]]
  (and (> a b) (> c b)))
```

e.g.

```clojure
(local-max? (take 3 sample-data))
=> false
```

Finally we have:

```clojure
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
```

```clojure
(inflection-points sample-data)
=> ([25.9 589014 :peak] [23.8 691995 :valley] [24.7 734902 :peak] [23.1 836204 :valley])
```

Use Clojure's **cycle** function to transform **sample-data** into a circuit that our jogger runs over and over again:

```clojure
(take 15 (inflection-points (cycle sample-data)))
```

## Collection *rest* and *next*

Quick note:

**rest** returns an empty list, while **next** returns **nil**.

and another note on **infinite collections**:

We often repeat expressions such as **(first my-seq)** inside a function when it might be tempting to use a local **let** binding instead. This is a way of avoiding references that would prevent a sequence from being garbage collected.