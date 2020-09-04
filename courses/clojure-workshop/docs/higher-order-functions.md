# Higher Order Functions

```clojure
(update {:item "Tomato" :price 1.0} :price (fn [x] (/ x 2)))
=> {:item "Tomato", :price 0.5}

(update {:item "Tomato" :price 1.0} :price / 2)
=> {:item "Tomato", :price 0.5}

(update {:item "Tomato" :fruit false} :fruit not)
=> {:item "Tomato", :fruit true}
```

## Higher Order Function - Function That Receives a Function

Let's create a simple higher order function. Our function **operate** will take another function to be applied:

```clojure
(defn operate [f x] (f x))
=> #'clojure-backwards.chap3/operate

(operate inc 2)
=> 3
```

Not very useful. More of a PoC of a function receiving a function i.e. a **higher order function**.

What about more parameters, or vardiac? At first we run into an issue:

```clojure
(defn operate [f & args] (f args))
=> #'clojure-backwards.chap3/operate

(operate + 1 2 3)
Execution error (ClassCastException) at java.lang.Class/cast (Class.java:3734).
Cannot cast clojure.lang.ArraySeq to java.lang.Number
```

The issue? We applied the **f** function to the **args** sequence directly, when what we really wanted was to apply **f** using each element of the sequence as an argument. There is a special function to disassemble a sequence and apply a function to that sequence's elements – the **apply** function:

```clojure
(+ 1 2 3)
=> 6

(+ [1 2 3])
Execution error (ClassCastException) at java.lang.Class/cast (Class.java:3734).
Cannot cast clojure.lang.PersistentVector to java.lang.Number

(apply + [1 2 3])
=> 6
```

And so we can fix our higher order function:

```clojure
(defn operate [f & args] (apply f args))
=> #'clojure-backwards.chap3/operate

(operate str "It " "should " "concatenate")
=> "It should concatenate"
```

## Higher Order Function - Function That Returns a Function

```clojure
(defn random-fn [] (first (shuffle [+ - * /])))
=> #'clojure-backwards.chap3/random-fn

((random-fn) 2 3)
=> 2/3

((random-fn) 2 3)
=> 6
```

You can use the **fn?** function to check whether a value passed as a parameter is a function:

```clojure
(fn? random-fn)
=> true

(fn? (random-fn))
=> true
```

In this case, observe that both **random-fn** and the value returned by **random-fn** are functions. So, we can call the function returned by **random-fn**, and even bind it to a symbol, as in the example that follows, where we bind the function to the **mysterious-fn** symbol:

```clojure
(let [mysterious-fn (random-fn)]
  (mysterious-fn 2 3))
=> 5
```

## Partial Functions

```clojure
(def marketing-adder (partial + 0.99))
=> #'clojure-backwards.chap3/marketing-adder

(marketing-adder)
=> 0.99

(marketing-adder 10 5)
=> 15.99
```

Calling **(partial + 0.99)** returns a new function that we bind to the **marketing-adder** symbol. When **marketing-adder** is called, it will call **+** with **0.99** and any extra arguments passed to the function. Notice that we used **def** and not **defn**, because we don't need to build a new function – **partial** does it for us.

## Composing Functions

Suppose we have the following:

```clojure
(defn sample [coll] (first (shuffle coll)))
=> #'clojure-backwards.chap3/sample

(sample [1 2 3 4])
=> 4
```

This can be improved with **composition** - where composition runs **right to left**:

```clojure
(def sample (comp first shuffle))
=> #'clojure-backwards.chap3/sample

(sample [1 2 3 4])
=> 3
```

This **right to left** matters as shown:

```clojure
((comp inc *) 2 2)
=> 5

((comp * inc) 2 2)
Execution error (ArityException) at clojure-backwards.chap3/eval1766 (form-init9704642767572168928.clj:1).
Wrong number of args (2) passed to: clojure.core/inc
```

Notice that when providing **inc** as the last argument of the **comp** function, it calls **(inc 2 2)**, which does not work because **inc** takes only one argument.

**Example of using both partial and comp**:

```clojure
(def marketing-adder (partial + 0.99))
=> #'clojure-backwards.chap3/marketing-adder

(def format-price (partial str "£"))
=> #'clojure-backwards.chap3/format-price

(def checkout (comp (partial str "Only ") format-price marketing-adder))
=> #'clojure-backwards.chap3/checkout

(checkout 10 5 15 6 9)
=> "Only £45.99"
```

## Anonymous Functions

**#()** is a shorter way of writing an anonymous function.

Parameters are not named and therefore parameter values can be accessed in order with **%1**, **%2**, **%3**, and so on.

When only one argument is provided, you can simply use **%** (omitting the argument number) to retrieve the value of the argument.

These are equivalent:

```clojure
(fn [s] (str "Hello " s))
=> #object[clojure_backwards.chap3$eval1780$fn__1781 0x5fdf098 "clojure_backwards.chap3$eval1780$fn__1781@5fdf098"]

((fn [s] (str "Hello " s)) "Bob")
=> "Hello Bob"

#(str "Hello " %)
=> #object[clojure_backwards.chap3$eval1793$fn__1794 0x2efe3006 "clojure_backwards.chap3$eval1793$fn__1794@2efe3006"]

(#(str "Hello " %) "Bob")
=> "Hello Bob"
```

And these are also equivalent:

```clojure
(fn [x y] (* (+ x 10) (+ y 20)))
=> #object[clojure_backwards.chap3$eval1806$fn__1807 0x696bfe10 "clojure_backwards.chap3$eval1806$fn__1807@696bfe10"]

#(* (+ %1 10) (+ %2 20))
=> #object[clojure_backwards.chap3$eval1814$fn__1815 0x5b23499 "clojure_backwards.chap3$eval1814$fn__1815@5b23499"]
```

As a sidenote:

**A dispatch table is a table of pointers to functions.**