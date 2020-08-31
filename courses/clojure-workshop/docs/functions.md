# Functions

Destructuring:

```clojure
(def airport {:lat 48.9615, :lon 2.4372, :code 'LFPB', :name "Paris Le Bourget Airport"})
=> #'clojure-backwards.chap3/airport

(defn print-coords [airport]
  (let [{:keys [lat lon name]} airport]
    (println (str name " is located at Latitude: " lat " - " "Longitude: " lon))))
=> #'clojure-backwards.chap3/print-coords

(print-coords airport)
Paris Le Bourget Airport is located at Latitude: 48.9615 - Longitude: 2.4372
=> nil
```

Example of flight data destructured:

```clojure
(def booking [
  1425,
  "Bob Smith",
  "Allergic to unsalted peanuts only",
  [[48.9615, 2.4372], [37.742, -25.6976]],
  [[37.742, -25.6976], [48.9615, 2.4372]]
])

(let [[id customer-name sensitive-info flight1 flight2 flight3] booking]
  (println id customer-name flight1 flight2 flight3))
=> 1425 Bob Smith [[48.9615 2.4372] [37.742 -25.6976]] [[37.742 -25.6976] [48.9615 2.4372]] nil
```

We don't care about "id" and should ignore "sensitive information" so use **_**

```clojure
(let [[_ customer-name _ flight1 flight2 flight3] booking]
  (println customer-name flight1 flight2 flight3))
=> Bob Smith [[48.9615 2.4372] [37.742 -25.6976]] [[37.742 -25.6976] [48.9615 2.4372]] nil
```

NOTE - **nil** because there is no "flight3".

There's another aspect of destructuring that we could use: the "remaining" parts of the sequence. By using the **&** character followed by a symbol, we can bind the remaining part of a sequence to a given symbol:

```clojure
(let [[_ customer-name _ & flights] booking]
  (println (str customer-name " booked " (count flights) " flights.")))
=> Bob Smith booked 2 flights.
```

```clojure
(defn print-flight [flight]
  (let [[[lat1 lon1] [lat2 lon2]] flight]
    (println (str "Flying from: Lat " lat1 " Lon " lon1 ", Flying to: Lat " lat2 " Lon " lon2))))

(print-flight [[48.9615 2.4372] [37.742 -25.6976]])
=> Flying from: Lat 48.9615 Lon 2.4372, Flying to: Lat 37.742 Lon -25.6976
```

Well that destructuring is again getting difficult to read, but we can do better.

```clojure
(defn print-flight [flight]
  (let [[departure arrival] flight
        [lat1 lon1] departure
        [lat2 lon2] arrival]
    (println (str "Flying from: Lat " lat1 " Lon " lon1 ", Flying to: Lat " lat2 " Lon " lon2))))

(print-flight [[48.9615 2.4372] [37.742 -25.6976]])
```

Final version for our flights:

```clojure
(defn print-booking [booking]
  (let [[_ customer-name _ & flights] booking]
    (println (str customer-name " booked " (count flights) " flights."))

    (let [[flight1 flight2 flight3] flights]
      (when flight1 (print-flight flight1))
      (when flight2 (print-flight flight2))
      (when flight3 (print-flight flight3)))))
```

## Arity Overloading

```clojure
(defn no-overloading []
  (println "Same old, same old..."))
=> #'clojure-backwards.chap3/no-overloading

(defn overloading
  ([] "No argument")
  ([a] (str "One argument: " a))
  ([a b] (str "Two arguments: a: " a " b: " b)))
=> #'clojure-backwards.chap3/overloading

(overloading)
=> "No argument"

(overloading 1)
=> "One argument: 1"

(overloading 1 2)
=> "Two arguments: a: 1 b: 2"

(overloading 1 nil)
=> "Two arguments: a: 1 b: "

(overloading 1 2 3)
Execution error (ArityException) at clojure-backwards.chap3/eval1643 (form-init9403474688150680813.clj:1).
Wrong number of args (3) passed to: clojure-backwards.chap3/overloading
```

## Variadic Functions

