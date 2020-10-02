# Host Platform

## Import Packages and Java Classes

In the REPL:

```clojure
(import 'java.math.BigDecimal)
=> java.math.BigDecimal

(new BigDecimal "100000")
=> 100000M
```

In code:

```clojure
(ns clojure-backwards.chap9
  (:import
    (java.math BigDecimal BigInteger)
    (java.time LocalTime)
    (java.util Locale)))

(new BigDecimal "100000")
```

Watch that **space** after **java.math**.

Using a dot, **.**. we can place a dot after a class name. Clojure assumes that we want to construct an instance of a class e.g.

```clojure
(BigDecimal. 100000.5)
```

Importing multiple classes from same package:

```clojure
(import '[java.math BigDecimal BigInteger])
=> java.math.BigInteger

(BigInteger. "10000")
=> 10000
```

From different packages:

```clojure
(import 'java.time.LocalTime 'java.util.Locale)
```

What about Java static methods e.g.

```java
LocalTime time = LocalTime.now();
```

We can also access static methods from the **LocalTime** class in Clojure. This is done by stating the class followed by a slash and a method name:

```clojure
(LocalTime/now)
=> #object[java.time.LocalTime 0x23341e8b "17:52:19.873"]
```

Accessing instance methods, we use the dot operator with a function name:

```clojure
(.negate big-num)
=> -10000M
```

This was an example of calling a function without any arguments. In the following example, we will see how to call an instance method that accepts arguments.

In Java:

```java
BigDecimal big_num = new BigDecimal("100000");
big_num.pow(2);
```

In Clojure:

```clojure
(.pow big-num 2)
=> 100000000M
```

Java often chains method calls. This can become a tad awkward in Clojure:

```clojure
(import 'java.time.ZonedDateTime)
=> java.time.ZonedDateTime

(ZonedDateTime/now)
=> #object[java.time.ZonedDateTime 0x3bf9831e "2020-09-30T18:16:47.608+01:00[Europe/London]"]

(.getOffset (ZonedDateTime/now))
=> #object[java.time.ZoneOffset 0x3ceed87 "+01:00"]

(. (ZonedDateTime/now) getOffset)
=> #object[java.time.ZoneOffset 0x3ceed87 "+01:00"]

(. (. (ZonedDateTime/now) getOffset) getTotalSeconds)
=> 3600

(.getTotalSeconds (.getOffset (ZonedDateTime/now)))
=> 3600
```

The **..** helps:

```clojure
(.. (ZonedDateTime/now) getOffset getTotalSeconds)
=> 3600
```

What about this Java code to avoid instantiating lots of String only to be garbage collected?

```java
StringBuffer string = new StringBuffer("quick");
string.append("brown");
string.append("fox");
...
```

In Clojure:

```clojure
(let [string (StringBuffer. "quick")]
			(.append string " brown")
			(.append string " fox")
			...
     	(.toString string))
```

There is a repetition of the word **string**. The **doto** macro eliminates this duplication. The **doto** macro will implicitly call functions on instances that we specify. The preceding code can be rewritten using **doto**:

```clojure
(let [string (StringBuffer. "quick")]
     (doto string
           (.append " brown")
           (.append " fox")
					 ...
           (.append " dog"))

     (.toString string))
```

## Working with Java Data Types

Collections:

```clojure
(def capitals ["Berlin" "Oslo" "Warszawa" "Belgrad"])
=> #'clojure-backwards.chap3/capitals

capitals
=> ["Berlin" "Oslo" "Warszawa" "Belgrad"]

(class capitals)
=> clojure.lang.PersistentVector

; Using Clojure's vector, we can create an ArrayList in Java:
(def destinations (java.util.ArrayList. capitals))
=> #'clojure-backwards.chap3/destinations
; We created an ArrayList from a vector.

destinations
=> ["Berlin" "Oslo" "Warszawa" "Belgrad"]

(class destinations)
=> java.util.ArrayList

; We can also convert the other way - We can convert from Java to Clojure as follows:
(vec destinations)
=> ["Berlin" "Oslo" "Warszawa" "Belgrad"]

(class (vec destinations))
=> clojure.lang.PersistentVector
```

Hash:

```clojure
(def fluss {"Germany" "Rhein" "Poland" "Vistula"})
=> #'clojure-backwards.chap3/fluss

(class fluss)
=> clojure.lang.PersistentArrayMap

(def rivers (java.util.HashMap. fluss))
=> #'clojure-backwards.chap3/rivers

rivers
=> {"Poland" "Vistula", "Germany" "Rhein"}

(class rivers)
=> java.util.HashMap

; Using HashMap from Java, we can create a hash in Clojure:
(into {} rivers)
=> {"Poland" "Vistula", "Germany" "Rhein"}

(class (into {} rivers))
=> clojure.lang.PersistentArrayMap
```

## EDN

SIDENOTE:

> [Extensible Data Notation](https://github.com/edn-format/edn) is often used by Clojure.

## Using JavaScript in ClojureScript

In order to access a method from a JavaScript object, we place **.** (a dot) followed by a method name. Accessing a field of an object is very similar. We use **.-** (a dot and a hyphen) before the field name. You might wonder why accessing a function uses slightly different syntax than accessing a field. In JavaScript, an object can have a method and a field with the same name. In ClojureScript, we need a way to distinguish between a function call and a field access.

In JavaScript, the code looks as follows:

```javascript
var string = "JavaScript string"

var string_length = string.length;

var shout = string.toUpperCase();
```

In ClojureScript, the code looks as follows:

```clojure
(def string "JavaScript string")

(def string_length (.-length string))

(def shout (.toUpperCase string))
```

ClojureScript, as with Clojure, just remember:

<u>**Unless we have a sequence, the first position in a list is treated as a function**</u>

```clojure
➜ planck
ClojureScript 1.10.597

cljs.user=> (def string "JavaScript string")
#'cljs.user/string

cljs.user=> (.-length string)
17
```

JavaScript (is a bit rubbish) puts everything in global (only patterns help to modularise).

ClojureScript allows the use of namespaces in JavaScript. We should pay attention to one namespace. ClojureScript uses the **js** namespace to refer to the global scope of a program. Core JavaScript objects such as **Number**, **String**, and **Date** are accessed in ClojureScript using the **js** namespace. In this namespace, we will also find browser-defined objects such as **window**.

In order to construct a JavaScript object, we use the object's name followed by a dot. This is the same syntax we used to construct an instance of a Java class in Clojure.

JavaScript:

```javascript
var num = new Number(123);
```

ClojureScript:

```clojure
(def num (js/Number. 123))
```

## Working with JavaScript Data Types

Create a ClojureScript project:

```bash
lein new mies js-interop
```

```bash
workspace/clojure/js-interop 
➜ scripts/repl
ClojureScript 1.10.339
cljs.user=> 
```

ClojureScript provides the **js-obj** function for creating a JavaScript object from ClojureScript data:

```clojure
cljs.user=> (js-obj "Austria" "Donau")
#js {:Austria "Donau"}
```

Calling the **js-obj** function created a new JavaScript object. Notice the **#js** in the REPL. This symbol in the REPL informs us that the following expression is a JavaScript object.

```clojure
cljs.user=> (def rivers-map-js (js-obj "country" {"river" "Donau"}))
#'cljs.user/rivers-map-js

cljs.user=> (.-country rivers-map-js)
{"river" "Donau"}
```

Alas, we cannot access data using JavaScript interoperability. The reason is because the **js-obj** function is shallow. It does not transform nested data structures to JavaScript objects.

```clojure
cljs.user=> (.-river (.-country rivers-map-js))
nil
```

In order to transform all nested data, we need to use the **clj->js** function:

```clojure
cljs.user=> (def rivers-map-js-converted (clj->js {"country" {"river" "Donau"}}))
#'cljs.user/rivers-map-js-converted

cljs.user=> rivers-map-js-converted
#js {:country #js {:river "Donau"}}
```

Aha! Nested **#js**, and now it works:

```clojure
cljs.user=> (.-river (.-country rivers-map-js-converted))
"Donau"
```

It is possible to convert the other way, from JavaScript to ClojureScript:

```clojure
cljs.user=> (js->clj #js {:river "Donau"})
{"river" "Donau"}
```

Convert nested JavaScript objects to ClojureScript data:

```clojure
cljs.user=> (js->clj #js {:country #js {:river "Donau"}})
{"country" {"river" "Donau"}}
```

Every time we have a JavaScript object, we have to mark it using the **#js** symbol. This instructs ClojureScript to treat the following data as a JavaScript object.

## Figwheel Template

Figwheel is a tool that compiles ClojureScript code.

## Reactive Web Programming with Rum

Rum is a library used to create HTML elements on a page using application state.

HTML provides a structure of elements on a page. The **Document Object Model** (**DOM**) is a representation of HTML in JavaScript. JavaScript allows us to operate on DOM elements that are finally displayed as HTML elements on a web page.

**React.js** is a JavaScript library that supports reactive programming. The basic block of React.js is a component. In React.js, we define what components should look like and how they should behave. With React.js, we can create components based on the current application state. Changes in state result in re-rendering components that require a change. React internally checks which parts of the application state have changed and which components rely on these parts of state. As a result, React re-renders only those components that used parts of the application state that have changed.

Rum is a Clojure library for creating HTML elements on a web page. Rum is based on React.js.

## Figwheel and Rum

Create a project:

```bash
~/workspace/clojure
➜ lein new figwheel-main hello-clojurescript.core -- --rum

Retrieving figwheel-main/lein-template/0.2.11/lein-template-0.2.11.pom from clojars
Retrieving figwheel-main/lein-template/0.2.11/lein-template-0.2.11.jar from clojars
Generating fresh figwheel-main project.
  To get started:
  -->  Change into the 'hello-clojurescript.core' directory
  -->  Start build with 'lein fig:build'
```

Another project utilising jQuery for drag and drop:

```bash
lein new figwheel-main hello-drag-and-drop -- --rum
```

In the project, we will use the **jayq** external library. **jayq** is a ClojureScript wrapper for jQuery.

## Exceptions and Errors in Clojure

In Java the most common example of a **try-catch-finally** block is reading or writing to a file. Inside the **try** block, we have an operation to read or write to a file. The **catch** block would guard against IO exceptions such as file not present. In the **finally** block, we would have code to close the file. Closing the file releases computer resources for other tasks.

This cleanup is common enough also in Clojure to warrant the **with-open** macro to release an IO resource.

```clojure
(def three-numbers-array (java.util.ArrayList. [0 1 2]))
=> #'clojure-backwards.chap3/three-numbers-array

(defn array-list-getter [array index]
  (try
    (.get array index)
    (catch IndexOutOfBoundsException ex
      (str "No element at index " index ", because of, " (.getMessage ex)))
    (finally
      (println "Done with array-list-getter"))))
=> #'clojure-backwards.chap3/array-list-getter

(array-list-getter three-numbers-array 5)
Done with array-list-getter
=> "No element at index 5, because of, Index: 5, Size: 3"

```

## Errors in JavaScript

JavaScript does not distinguish between errors and exceptions, so any situations in which code causes the application not to run as expected are errors.

```clojure
➜ planck
ClojureScript 1.10.597

cljs.user=> (def languages {:Clojure "CLJ"
                            :ClojureScript "CLJS"
                            :JavaScript "JS"})
#'cljs.user/languages

cljs.user=> (defn language-abbreviator [language]
              (if-let [lang (get languages language)]
                lang
                (throw (js/Error. "Language not supported"))))
#'cljs.user/language-abbreviator

cljs.user=> (language-abbreviator :JavaScript)
"JS"

cljs.user=> (language-abbreviator :Ruby)
Execution error (Error) at (<cljs repl>:1).
Language not supported

cljs.user=> (defn get-language-of-the-week [languages]
              (let [lang-of-the-week (rand-nth languages)]
                (try
                  (str "The language of the week is: " (language-abbreviator lang-of-the-week))
            
                  (catch js/Error e
                    (str lang-of-the-week " is not a supported language"))
            
                  (finally (println lang-of-the-week "was chosen as the language of the week")))))
#'cljs.user/get-language-of-the-week

cljs.user=> (get-language-of-the-week [:Ruby :Kotlin :Go])
:Ruby was chosen as the language of the week
":Ruby is not a supported language"
```

