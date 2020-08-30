# Data Type & Immutablity

Clojure is a hosted language and has three notable, major implementations in Java, JavaScript, and .NET. Being a hosted language is a useful trait that allows Clojure programs to run in different environments and take advantage of the ecosystem of its host. Regarding data types, it means that each implementation has different underlying data types.

## String

Most functions for manipulating strings can be found in the **clojure.string** namespace. Here is a list of them using the REPL **dir**function:

```clojure
(dir clojure.string)
blank?
capitalize
ends-with?
escape
includes?
index-of
join
last-index-of
lower-case
re-quote-replacement
replace
replace-first
reverse
split
split-lines
starts-with?
trim
trim-newline
triml
trimr
upper-case
=> nil
```

e.g.

```clojure
(clojure.string/includes? "potatoes" "toes")
=> true
```

```clojure
(clojure.string/replace "Hello World" #"\w" (fn [letter] (do (println letter) "!")))
H
e
l
l
o
W
o
r
l
d
=> "!!!!! !!!!!"
```

```clojure
str
=> #object[clojure.core$str 0x537ed2e "clojure.core$str@537ed2e"]
```

Those gibberish-like values are string representations of the functions, because we are asking for the values bound to the symbols rather than invoking the functions (wrapping them with parentheses).

## Number

```clojure
(type 1)
=> java.lang.Long

(type 10000000000000000000)
=> clojure.lang.BigInt
```

Notice, in the preceding example, that the number was too big to fit in the **java.lang.Long** Java type and, therefore, was implicitly typed **clojure.lang.BigInt**.

Ratios:

```clojure
5 / 4
=> 5
=> #object[clojure.core$_SLASH_ 0x419eaa08 "clojure.core$_SLASH_@419eaa08"]
=> 4

5/4
=> 5/4
```

```clojure
(/ 3 4)
=> 3/4

(type 3/4)
=> clojure.lang.Ratio
```

If we take our division of 3 by 4 again, but this time mix in a "Double" type, we will not get a ratio as a result:

```clojure
(/ 3 4.0)
=> 0.75
```

```clojure
Math/PI
=> 3.141592653589793

(Math/random)
=> 0.8002062474443027

(Math/sqrt 9)
=> 3.0
```

## Keywords

You can think of a **keyword** as some kind of a special constant string.

```clojure
:foo
=> :foo
```

They don't refer to anything else like symbols do; as you can see in the preceding example, when evaluated, they just return themselves. Keywords are typically used as keys in a key-value associative map.

## Collections

Clojure is a functional programming language in which we focus on building the computations of our programs in terms of the evaluation of functions, rather than building custom data types and their associated behaviors.

The four main data structures for collections that you should know about are **Maps**, **Sets**, **Vectors**, and **Lists**.

## Maps

```clojure
{:artist "David Bowtie" :song "The Man Who Mapped the World" :year 1970}
=> {:artist "David Bowtie", :song "The Man Who Mapped the World", :year 1970}
```

For maps, there's no best practice and if you think it improves a map's readability, use commas; otherwise, simply omit them. You can also separate entries with new lines.

Example of nesting and different structures:

```clojure
{
  "David Bowtie" {
    "The Man Who Mapped the World" {:year 1970, :duration "4:01"}
    "Comma Oddity" {:year 1969, :duration "5:19"}
  }
  "Crosby Stills Hash" {
    "Helplessly Mapping" {:year 1969, :duration "2:38"}
    "Almost Cut My Hair" {:year 1970, :duration "4:29", :featuring ["Neil Young", "Rich Hickey"]}
  }
}
=>
{"David Bowtie" {"The Man Who Mapped the World" {:year 1970, :duration "4:01"}, "Comma Oddity" {:year 1969, :duration "5:19"}},
 "Crosby Stills Hash" {"Helplessly Mapping" {:year 1969, :duration "2:38"}, "Almost Cut My Hair" {:year 1970, :duration "4:29", :featuring ["Neil Young" "Rich Hickey"]}}}
```

We can use the **hash-map** function directly:

```clojure
(hash-map :a 1 :b 2 :c 3)
=> {:c 3, :b 2, :a 1}
```

Accessing a Map:

```clojure
(def favourite-fruit
  {:name "Kiwi" :color "Green" :kcal-per-100g 61 :distinguishing-mark "Hairy"})
=> #'clojure-backwards.chap2/favourite-fruit

(get favourite-fruit :name)
=> "Kiwi"

(get favourite-fruit :color)
=> "Green"

(get favourite-fruit :taste)
=> nil

(get favourite-fruit :taste "Very good 8/10")
=> "Very good 8/10"
```

Maps and keywords have the special ability to be used as functions. When positioned in the "operator position" (as the first item of the list), they are invoked as a function that can be used to look up a value in a map:

```clojure
(favourite-fruit :color)
=> "Green"

(:color favourite-fruit)
=> "Green"

(:shape favourite-fruit "egg-like")
=> "egg-like"
```

As shown, even using a keyword as a function is allowed.

Use **assoc** to associate a new key, **:shape**, with a new value, **"egg-like"**, in our map:

```clojure
(assoc favourite-fruit :shape "egg-like")
=> {:name "Kiwi", :color "Green", :kcal-per-100g 61, :distinguishing-mark "Hairy", :shape "egg-like"}
```

When we **assoc** we of course generate a new Map - **assoc** replaces the existing value when a key already exists, because HashMaps cannot have duplicate keys:

```clojure
(assoc favourite-fruit :color "Brown")
=> {:name "Kiwi", :color "Brown", :kcal-per-100g 61, :distinguishing-mark "Hairy"}
```

```clojure
(assoc favourite-fruit :yearly_production_in_tonnes {:china 2025000 :italy 541000 :new_zealand 412000 :iran 311000 :chile 225000})
=> {:name "Kiwi", :color "Green", :kcal-per-100g 61, :distinguishing-mark "Hairy", :yearly_production_in_tonnes {:china 2025000, :italy 541000, :new_zealand 412000, :iran 311000, :chile 225000}}
```

Let's decrement **kcal-per-100g** with the **assoc** function (by using a function) as follows:

```clojure
(assoc favourite-fruit :kcal-per-100g (- (:kcal-per-100g favourite-fruit) 1))
=> {:name "Kiwi", :color "Green", :kcal-per-100g 60, :distinguishing-mark "Hairy"}
```

While the **assoc** function lets you associate a completely new value to a key, **update** allows you to compute a new value based on the previous value of a key:

```clojure
(update favourite-fruit :kcal-per-100g dec)
=> {:name "Kiwi", :color "Green", :kcal-per-100g 60, :distinguishing-mark "Hairy"}

(update favourite-fruit :kcal-per-100g - 10)
=> {:name "Kiwi", :color "Green", :kcal-per-100g 51, :distinguishing-mark "Hairy"}
```

Finally, use **dissoc** (as in "dissociate") to remove one or multiple elements from a map:

```clojure
(dissoc favourite-fruit :color :kcal-per-100g)
=> {:name "Kiwi", :distinguishing-mark "Hairy"}
```

## Sets

```clojure
#{1 2 3 4 5}
=> #{1 4 3 2 5}
```

NOTE - The value is transformed in a unique hash, which allows fast access but does not keep the insertion order.

```clojure
(hash-set :a :b :c :d)
=> #{:c :b :d :a}
```

Hash Sets can also be created from another collection with the **set** function. Let's create a HashSet from a vector:

```clojure
(set [:a :b :c])
=> #{:c :b :a}
```

And the **sorted** version:

```clojure
(sorted-set "No" "Copy" "Cats" "Cats" "Please")
=> #{"Cats" "Copy" "No" "Please"}
```

Accessing:

```clojure
(def supported-currencies #{"Dollar", "Japanese yen" "Euro" "Indian rupee" "British pound"})
=> #'clojure-backwards.chap2/supported-currencies

(get supported-currencies "Dollar")
=> "Dollar"

(get supported-currencies "Swiss franc")
=> nil

(contains? supported-currencies "Dollar")
=> true

(contains? supported-currencies "Swiss franc")
=> false
```

As with maps, sets and keywords can be used as functions to check for containment. Use the **supported-currencies** set as a function to look up a value in the set:

```clojure
(supported-currencies "Swiss franc")
=> nil
```

NOTE - We cannot use strings as a function to look up a value in a set or a Map. That's one of the reasons why keywords are a better choice in both sets and maps when possible:

```clojure
("Dollar" supported-currencies)
Execution error (ClassCastException) at clojure-backwards.chap2/eval1678 (form-init12327341918443297422.clj:1).
class java.lang.String cannot be cast to class clojure.lang.IFn (java.lang.String is in module java.base of loader 'bootstrap'; clojure.lang.IFn is in unnamed module of loader 'app')
```

To add an entry to a set, use the **conj** function, as in "conjoin":

```clojure
(conj supported-currencies "Monopoly Money" "Gold dragan" "Gil")
=> #{"Japanese yen" "Indian rupee" "Euro" "Dollar" "Monopoly Money" "Gold dragan" "British pound" "Gil"}
```

Finally, you can remove one or more items with the **disj**function, as in "disjoin":

```clojure
(disj supported-currencies "Dollar" "British pound")
=> #{"Japanese yen" "Indian rupee" "Euro"}
```

## Vectors

They are collections of values efficiently accessible by their integer index (starting from 0), and they maintain the order of item insertion as well as duplicates.

```clojure
[1 2 3]
=> [1 2 3]

(vector 1 2 3)
=> [1 2 3]

[nil :keyword "String" {:answers [:yep :nope]}]
=> [nil :keyword "String" {:answers [:yep :nope]}]
```

You can create a vector from another collection using the **vec** function:

```clojure
(vec #{1 2 3})
=> [1 3 2]
```

Accessing:

```clojure
(get [:a :b :c] 0)
=> :a

(get [:a :b :c] 10)
=> nil
```

As with maps and sets, you can use the vector as a function to look up items:

```clojure
(def fibonacci [0 1 1 2 3 5 8])
=> #'clojure-backwards.chap2/fibonacci

(fibonacci 6)
=> 8
```

```clojure
(conj fibonacci 13 21)
=> [0 1 1 2 3 5 8 13 21]
```

```clojure
(let [size (count fibonacci)
      last-number (last fibonacci)
      second-to-last-number (fibonacci (- size 2))]
  (conj fibonacci (+ last-number second-to-last-number)))
=> [0 1 1 2 3 5 8 13]
```

## Lists

Lists are sequential collections, similar to vectors, but items are added to the front (at the beginning). Also, they don't have the same performance properties, and random access by index is slower than with vectors. We mostly use lists to write code and macros, or in cases when we need a **last-in, first-out** (**LIFO**) type of data structure (for example, a stack), which can arguably also be implemented with a vector.

We create lists with the literal syntax, **()**, but to differentiate lists that represent code and lists that represent data, we need to use the single quote, **'**:

```clojure
(1 2 3)
Execution error (ClassCastException) at clojure-backwards.chap2/eval1590 (form-init6058892892036525798.clj:1).
class java.lang.Long cannot be cast to class clojure.lang.IFn (java.lang.Long is in module java.base of loader 'bootstrap'; clojure.lang.IFn is in unnamed module of loader 'app')

(+ 1 2 3)
=> 6

'(+ 1 2 3)
=> (+ 1 2 3)
```

```clojure
(list :a :b :c)
=> (:a :b :c)

(first '(:a :b :c))
=> :a

(rest '(:a :b :c))
=> (:b :c)
```

You cannot use **get** but can use **nth** though it is inefficient since it walks from first through the list:

```clojure
(nth '(:a :b :c :d) 2)
=> :c
```

```clojure
(def my-todo (list "Feed the cat" "Clean the bathroom" "Save the world"))
=> #'clojure-backwards.chap2/my-todo

(cons "Go to work" my-todo)
=> ("Go to work" "Feed the cat" "Clean the bathroom" "Save the world")

(conj my-todo "Go to work" "Wash my socks")
=> ("Wash my socks" "Go to work" "Feed the cat" "Clean the bathroom" "Save the world")
```

## Collection and Sequence Abstractions

```clojure
(def language {:name "Clojure" :creator "Rich Hickey" :platforms ["Java" "JavaScript" ".NET"]})
=> #'clojure-backwards.chap2/language

(count language)
=> 3

(count #{})
=> 0

(empty? language)
=> false

(empty? [])
=> true
```

A map is not sequential because there is no logical order between its elements. However, we can convert a map to a sequence using the **seq** function:

```clojure
(seq language)
=> ([:name "Clojure"] [:creator "Rich Hickey"] [:platforms ["Java" "JavaScript" ".NET"]])

(nth (seq language) 1)
=> [:creator "Rich Hickey"]
```

**into** is another useful operator that puts elements of one collection into another collection. The first argument for **into** is the target collection:

```clojure
(into [1 2 3 4] #{5 6 7 8})
=> [1 2 3 4 7 6 5 8]
```

A usage example would be, for example, to deduplicate a vector, just put it into a set:

```clojure
(into #{} [1 2 3 3 3 4])
=> #{1 4 3 2}
```

To put items into a map, you would need to pass a collection of tuples representing key-value pairs:

```clojure
(into {} [[:a 1] [:b 2] [:c 3]])
=> {:a 1, :b 2, :c 3}
```

Elements are added to a list at the front:

```clojure
(into '() [1 2 3 4])
=> (4 3 2 1)
```

**concat** maybe more appropriate, but note the difference with **into**:

```clojure
(concat '(1 2) '(3 4))
=> (1 2 3 4)

(into '(1 2) '(3 4))
=> (4 3 1 2)
```

**sort** has the benefit of being slightly more obvious in terms of why you would want a sequence as a result:

```clojure
(def alphabet #{:a :b :c :d :e :f})
=> #'clojure-backwards.chap2/alphabet

alphabet
=> #{:e :c :b :d :f :a}

(sort alphabet)
=> (:a :b :c :d :e :f)

(sort [3 7 5 1 9])
=> (1 3 5 7 9)
```

But what if you wanted a vector as a result? Well, now you know that you could use the **into** function:

```clojure
(into [] (sort [3 7 5 1 9]))
=> [1 3 5 7 9]
```

Finally:

```clojure
(assoc [:a :b :c :d] 2 :z)
=> [:a :b :z :d]
```

