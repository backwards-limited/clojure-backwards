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

```clojure
(defn welcome
  [player & friends]
  (println (str "Welcome to the Parenthmazes " player "!"))
  (when (seq friends)
    (println (str "Sending " (count friends) " friend request(s) to the following players: " (clojure.string/join ", " friends)))))

(welcome "Jon")
=> Welcome to the Parenthmazes Jon!

(welcome "Jon" "Arya" "Tyrion" "Petyr")
=> Welcome to the Parenthmazes Jon!
Sending 3 friend request(s) to the following players: Arya, Tyrion, Petyr
```

The **seq** function can be used to get a sequence from a collection. In the **welcome** function, we use the **seq** function to test whether a collection contains elements. That's because **seq** returns **nil** when the collection passed as a parameter is empty.

Instead of testing whether **friends** is empty, we could also take advantage of the multi-arity technique:

```clojure
(defn welcome
  ([player]
   (println (str "Welcome to Parentmazes (single-player mode), " player "!")))
  
  ([player & friends]
   (println (str "Welcome to Parenthmazes (multi-player mode), " player "!"))
   (println (str "Sending " (count friends) " friend request(s) to the following players: " (clojure.string/join ", " friends)))))
```

Back to destructuring - There is a nice/handy way to reference a destructured parameter (much like **@** in a Scala pattern match).

Let's start off with the following function:

```clojure
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
=> {:name Zulkaz, :health 150.0, :camp :trolls}

(def armored-enemy {:name "Zulkaz" :health 250 :armor 0.8 :camp :trolls})

(println (strike armored-enemy :sword))
=> {:name Zulkaz, :health 230.0, :armor 0.8, :camp :trolls}
```

We would like to use our associative destructuring technique to retrieve the **camp** and **armor** values directly from the function parameters, and reduce the amount of code in the function's body. The only problem we have is that we still need to return an updated version of the **target** entity, but how could we both destructure the **target** entity and keep a reference of the **target** parameter? Clojure has your back – you can use the special key **:as** to bind the destructured map to a specific name.

Also, the special key **:or** permits us to provide a default value for when a key that we want to extract isn't found (instead of binding to **nil**):

```clojure
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
```

## Multimethods

Clojure offers a way to implement polymorphism with multimethods. Polymorphism is the ability of a unit of code (in our case, functions) to behave differently in different contexts, for example, based on the shape of the data received by the code. In Clojure, we also call it *runtime polymorphism* because the method to call is determined at runtime rather than at compile time. A multimethod is a combination of a dispatch function and of one or more methods. The two main operators for creating those multimethods are **defmulti** and **defmethod**.

```clojure
(defmulti strike (fn [m] (get m :weapon)))
```

In the preceding code, we've created a multimethod called **strike**. The second argument is the dispatch function, which simply retrieves a weapon in a map passed as a parameter. Remember that keywords can be used as functions of a **HashMap**, so we can simply write **defmulti** as follows:

```clojure
(defmulti strike :weapon)
```

By coding the above in the REPL, something interesting arises:

```clojure
*ns*
=> #object[clojure.lang.Namespace 0x250c0898 "clojure-backwards.chap3"]

(defmulti strike (fn [m] (get m :weapon)))
=> #'clojure-backwards.chap3/strike

(defmulti strike :weapon)
=> nil

(ns-unmap 'clojure-backwards.chap3 'strike)
=> nil

(defmulti strike :weapon)
=> #'clojure-backwards.chap3/strike
```

The second time we define **strike** we get back **nil** because the function is already in the current namespace, we are essentially just redefining it. We can **ns-unmap** the **strike var** to then re-evaluate.

Let's now implement the **defmulti** with two **defmethod**:

```clojure
(defmethod strike :sword
  [{{:keys [:health]} :target}]
  (- health 100))

(defmethod strike :cast-iron-saucepan
  [{{:keys [:health]} :target}]
  (- health 100 (rand-int 50)))
```

```clojure
(strike {:weapon :sword :target {:health 200}})
=> 100

(strike {:weapon :cast-iron-saucepan :target {:health 200}})
=> 73
```

And when there is no appropriate implement:

```clojure
(strike {:weapon :spoon :target {:health 200}})
Execution error (IllegalArgumentException) at clojure-backwards.chap3/eval1758 (form-init13762022122878640139.clj:1).
No method in multimethod 'strike' for dispatch value: :spoon
```

We can define a **default**:

```clojure
(defmethod strike :default
  [{{:keys [:health]} :target}]
  health)

(strike {:weapon :spoon :target {:health 200}})
=> 200
```

**Example:**

```clojure
(def player {:name "Lea" :health 200 :position {:x 10 :y 10 :facing :north}})

; move multimethod
; The dispatch function should determine the dispatch value
; by retrieving the :facing value in the :position map of a player entity.
; The :facing value could be one of the following values :north, :south, :west, :east

; Without composition
(defmulti move #(:facing (:position %)))

; With composition
(defmulti move (comp :facing :position))

; So, we call "multi" with a player to move.
; multi applies its function to work out which "method" to dispatch this player to.
; It could be "north" - and indeed let's implement that one first.
; north receives the player that we called "multi" with.

(defmethod move :north
  [player]
  (update-in player [:position :y] inc))

; By moving a player north, its y value should be incremented
(move player)
=> {:name Lea, :health 200, :position {:x 10, :y 11, :facing :north}}

; Upon defining the other directions we can check
(move {:position {:x 10 :y 10 :facing :west}})
=> {:position {:x 9, :y 10, :facing :west}}
```

