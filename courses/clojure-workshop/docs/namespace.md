# Namespace

```bash
lein repl
```

Whichever namespace you start in, you can always change to the **default**.

```clojure
clojure-backwards.chap3=> *ns*
#object[clojure.lang.Namespace 0x53106b32 "clojure-backwards.chap3"]

clojure-backwards.chap3=> (in-ns 'user)
#object[clojure.lang.Namespace 0x6c2d3d5b "user"]

user=>
```

The last line, **user=>**, tells us that we are in the default **user**namespace. In this namespace, functions from the **clojure.core** namespace are available to us.

The **in-ns** function will create a new namespace if the requested namespace does not exist.

To access vars from one namespace in another namespace, we need to explicitly state which namespace the var comes from. E.g.

```clojure
user=> (in-ns 'new-namespace)
#object[clojure.lang.Namespace 0x6a1dfd31 "new-namespace"]

new-namespace=> (def fruits ["orange" "apple" "melon"])
#'new-namespace/fruits

new-namespace=> fruits
["orange" "apple" "melon"]

new-namespace=> (in-ns 'other-namespace)
#object[clojure.lang.Namespace 0x2a6a2124 "other-namespace"]

other-namespace=> fruits
Syntax error compiling at (/private/var/folders/mg/b2drd58d7_d3dn_01lsl2vk80000gn/T/form-init1754417881139427419.clj:1:1786).
Unable to resolve symbol: fruits in this context
```

The following will work:

```clojure
other-namespace=> new-namespace/fruits
["orange" "apple" "melon"]
```

## Importing Namespaces using the *refer* function

Import a new namespace using the **refer** function:

```clojure
other-namespace=> (clojure.core/refer 'new-namespace)
nil

other-namespace=> fruits
["orange" "apple" "melon"]
```

The **refer** function allows us to use optional keywords to control importing namespaces.

The keywords that we can use with **refer** are:

- **:only** allows us to import only the functions that we specify.
- **:exclude** allows us to exclude certain functions from being imported.
- **:rename** allows us to rename functions that we import. This sets an alias—a new name for a function.

Example using **:only**:

```clojure
other-namespace=> (in-ns 'garden)
#object[clojure.lang.Namespace 0x1bba88d "garden"]

garden=> (def vegetables ["cucumber" "carrot"])
#'garden/vegetables

garden=> (def fruits ["orange" "apple" "melon"])
#'garden/fruits

garden=> (in-ns 'shop)
#object[clojure.lang.Namespace 0x3a368e31 "shop"]

shop=> (clojure.core/refer 'garden :only '(vegetables))
nil

shop=> vegetables
["cucumber" "carrot"]

shop=> fruits
Syntax error compiling at (/private/var/folders/mg/b2drd58d7_d3dn_01lsl2vk80000gn/T/form-init1754417881139427419.clj:1:1786).
Unable to resolve symbol: fruits in this context
```

Example using **:exlude**:

```clojure
shop=> (in-ns 'market)
#object[clojure.lang.Namespace 0x7e728acf "market"]

market=> (clojure.core/refer 'garden :exclude '(vegetables))
nil

market=> fruits
["orange" "apple" "melon"]

market=> vegetables
Syntax error compiling at (/private/var/folders/mg/b2drd58d7_d3dn_01lsl2vk80000gn/T/form-init1754417881139427419.clj:1:1786).
Unable to resolve symbol: vegetables in this context

market=> garden/vegetables
["cucumber" "carrot"]
```

Example using **:rename**:

```clojure
market=> (in-ns 'shops)
#object[clojure.lang.Namespace 0x657448ee "shops"]

shops=> (clojure.core/refer 'garden :rename '{fruits voce})
nil

shops=> vegetables
["cucumber" "carrot"]

shops=> fruits
Syntax error compiling at (/private/var/folders/mg/b2drd58d7_d3dn_01lsl2vk80000gn/T/form-init1754417881139427419.clj:1:1786).
Unable to resolve symbol: fruits in this context

shops=> voce
["orange" "apple" "melon"]
```

## Importing Functions with *require* and *use*

```clojure
user=> (require 'clojure.pprint)
nil

user=> (clojure.pprint/print-table [{:text "Clojure"} {:text "is"} {:text "fun"}])
|   :text |
|---------|
| Clojure |
|      is |
|     fun |
nil
```

With the **:as** keyword, we shorten how we call functions. We do not need to write the full namespace but simply an alias that we choose:

```clojure
user=> (require '[clojure.pprint :as pprint])
nil

user=> (pprint/print-table [{:text "Clojure"} {:text "is"} {:text "fun"}])
```

We can use functions from this namespace without fully qualifying them via **use**:

```clojure
user=> (use 'clojure.pprint)
nil

user=> (print-table [{:text "Clojure"} {:text "is"} {:text "fun"}])
```

And variations...

```clojure
user=> (use '[clojure.string :only [split]])
nil

user=> (split "Clojure workshop" #" ")
["Clojure" "workshop"]
```

```clojure
user=> (use '[clojure.edn :rename {read-string string-read}])
nil

user=> (class (string-read "#inst \"1989-02-06T13:20:50.52Z\""))
java.util.Date
```

