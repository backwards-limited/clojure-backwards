# Testing

**Functional tests** try to capture the functional requirements of the software being tested.

Typically, functional testing involves the following steps:

1. Identifying what functions and features a software component has, based on the requirements specification document
2. Creating input data based on the requirements
3. Determining the expected output
4. Executing the tests
5. Comparing the expected results with the actual output

**Non-functional tests** check things that are not directly related to the functional requirements. To put it another way, non-functional tests are concerned with the way that software operates as a whole, rather than with the specific behaviors of the software or its components.

With non-functional tests, we are concerned with areas such as security, how a system behaves under various load conditions, whether it is user-friendly, and whether it provides localization to run in different countries.

**Unit testing** is the testing of an individual software component or module. In Clojure, a function is a good candidate for unit testing. A function is intended to perform one task at a time.

There are a number of concepts in testing, two of which are as follows:

- **Assertion**: A Boolean (true or false) expression. An assertion is a statement about a specific part of our program, which will be true or false.
- **Stub**: A temporary replacement for a part of a code or a concept. A stub simulates the behavior of the replaced software component.

The **clojure.test** framework is the default Clojure unit testing framework that comes with the Clojure standard library.

## Example

Say we have the following testing namespace:

```clojure
(ns coffee-app-.utils-test
  (:require [clojure.test :refer [deftest testing are is]]
            [coffee-app.core :refer [price-menu]]
            [coffee-app.utils :refer :all]))
```

- **are**: Allows you to test multiple testing scenarios

- **is**: Allows you to test a single testing scenario

- **deftest**: Defines a Clojure test

- **testing**: Defines an expression that will be tested

A test:

```clojure
(deftest calculate-coffee-price-test-with-single-is
  (testing "Single test with is macro."
    (is (= (calculate-coffee-price price-menu :latte 1)
           0.5))))
```

The **deftest** macro allows us to define tests. Each test is defined using the **testing** macro. The **testing** macro can be supplied with a string to provide a testing context.

The **is** macro is somewhat limiting e.g. if we want to test buying 1, 2 and 3 coffees:

```clojure
(deftest calculate-coffee-price-test-with-multiple-is
  (testing "Multiple tests with is macro."
    (is (= (calculate-coffee-price price-menu :latte 1) 0.5))
    (is (= (calculate-coffee-price price-menu :latte 2) 1.0))
    (is (= (calculate-coffee-price price-menu :latte 3) 1.5))))
```

We use the **is** macro when we want to test a single scenario and the **are** macro when we want to test more than one scenario:

```clojure
(deftest calculate-coffee-price-test-with-are
  (testing "Multiple tests with are macro"
    (are [coffees-hash coffee-type number-of-cups result]
      (= (calculate-coffee-price coffees-hash coffee-type number-of-cups) result)
      price-menu :latte 1 0.5
      price-menu :latte 2 1.0
      price-menu :latte 3 1.5)))
```

## Using the Expectations Testing Library

- We add a dependency for the **expectations** library [**expectations "2.1.10"**].
- **lein-expectations** is a Leiningen plugin that runs expectations tests from the command line **[lein-expectations "0.0.8"]**.

e.g. **project.clj**:

```clojure
(defproject coffee-app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [expectations "2.1.10"]]
  :plugins [[lein-expectations "0.0.8"]]
  :main ^:skip-aot coffee-app.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})

```

Example expectations:

```clojure
(ns coffee-app-with-testing.utils-test
  (:require [clojure.test :refer [deftest testing are is]]
            [expectations :refer [expect in]]
            [coffee-app-with-testing.core :refer [price-menu]]
            [coffee-app-with-testing.utils :refer :all]))

(expect 1.5 (calculate-coffee-price price-menu :latte 3))

(expect ClassCastException (calculate-coffee-price price-menu :latte "3"))

(expect Number (calculate-coffee-price price-menu :latte 3))

(expect {:latte 0.5} (in price-menu))
```

```bash
➜ lein expectations

Ran 4 tests containing 4 assertions in 2 msecs
0 failures, 0 errors.
```

## Unit Testing with the Midje Library

I the coffee app we would like to test **load-orders**. However, this function depends on **file-exists**, so we have a dependency. So do we have to first code and test **file-exists** before working on **load-orders**? We can get around this with a **top down** approach to testing with **midje**.

With a top-down approach, we can write working tests for the main tested function without implementing functions that are used by the tested function. We state that we want to test the **load-orders**function and that it uses the file-exist function but we do not need to have a full implement of file-exist. We merely need to say that we will use this function. This allows us to focus on a feature that we want to test without worrying about implementing all sub-steps.

First add to **project.clj**:

```clojure
(defproject coffee-app "0.1.0-SNAPSHOT"
  ...
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [expectations "2.1.10"]
                 [midje "1.9.4"]]
  ...
```

**Midje** uses the **fact** macro, which states certain facts about a future version of our test. The macro takes a single argument on both sides of the **=>** symbol. The **fact** macro states that the result from the left-hand side is to be expected on the right-hand side of the symbol:

```clojure
(ns coffee-app.utils-test
  (:require [clojure.test :refer [deftest testing are is]]
            [expectations :refer [expect in]]
            [midje.sweet :refer [=> fact provided unfinished]]
            [coffee-app.core :refer [price-menu]]
            [coffee-app.utils :refer :all]))

(fact (calculate-coffee-price price-menu :latte 3) => 3)
```

**Midje** supports autotesting in the REPL.

```clojure
➜ lein repl
...

coffee-app.core=> (use 'midje.repl)
Run `(doc midje)` for Midje usage.
Run `(doc midje-repl)` for descriptions of Midje repl functions.
nil

coffee-app.core=> (autotest)

======================================================================
Loading (coffee-app.utils coffee-app.core coffee-app.utils-test coffee-app.core-test)

FAIL at (utils_test.clj:30)
Expected:
3
Actual:
1.5
>>> Midje summary:
FAILURE: 1 check failed. 

>>> Output from clojure.test tests:

Ran 3 tests containing 7 assertions.
0 failures, 0 errors.
[Completed at 12:52:06]
true
```

Whenever we change our code, midje will now automatically run - our test fails - let's fix and automatically see the test pass:

```clojure
(fact (calculate-coffee-price price-menu :latte 3) => 1.5)
```

```clojure
coffee-app.core=> 
======================================================================
Loading (coffee-app.utils-test)
>>> Midje summary:
All checks (1) succeeded.

>>> Output from clojure.test tests:

Ran 2 tests containing 6 assertions.
0 failures, 0 errors.
[Completed at 12:56:16]
```

Let's go back to **top down** testing and dependencies. Say we want to test:

```clojure
(defn get-bought-coffee-message-with-currency [type number total currency]
  (format "Buying %d %s coffees for total: %s%s" number (name type) "€" total))
```

At the moment Euro is hard coded - the plan is to look this up with a function **get-currency** and our test expects that by stubbing:

```clojure
(fact "Message about number of bought coffees should include currency symbol"
     (get-bought-coffee-message-with-currency :latte 3 1.5 :euro) => "Buying 3 latte coffees for total: €1.5"
      (provided
        (get-currency :euro) => "€"))
```

Initially we shall get an error because **provided** also states that we expect the function **get-currency** to be called, but above we originally hard coded the currency:

```clojure
Loading (coffee-app.utils coffee-app.core coffee-app.utils-test coffee-app.core-test)

FAIL at (utils_test.clj:35)
These calls were not made the right number of times:
    (get-currency :euro) [expected at least once, actually never called]
>>> Midje summary:
FAILURE: 1 check failed.  (But 2 succeeded.)
```

Let's fix this:

```clojure
(def ^:const currencies {:euro {:countries #{"France" "Spain"} :symbol "€"}
                         :dollar {:countries #{"USA"} :symbol "$"}})

(defn get-currency [currency])

(defn get-bought-coffee-message-with-currency [type number total currency]
  (format "Buying %d %s coffees for total: %s%s" number (name type) (get-currency currency) total))
```

If we remove the **provided** (the stub), since our implementation of **get-currency** is incomplete, we would get:

```clojure
FAIL "Message about number of bought coffees should include currency symbol" at (utils_test.clj:33)
Expected:
"Buying 3 latte coffees for total: €1.5"
Actual:
"Buying 3 latte coffees for total: null1.5"
Diffs: strings have 1 difference (90% similarity)
                expected: "...otal: (€---)1.5"
                actual:   "...otal: (null)1.5"
>>> Midje summary:
FAILURE: 1 check failed.  (But 1 succeeded.)
```

## Property Based Testing

Clojure provides the **test.check** library for property-based testing. Add this to **project.clj**:

```clojure
(defproject coffee-app "0.1.0-SNAPSHOT"
  ...
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [expectations "2.1.10"]
                 [midje "1.9.4"]
                 [org.clojure/test.check "0.10.0"]]
	...
```

Example generator:

```clojure
coffee-app.core=> (gen/sample gen/small-integer)
(0 1 -2 1 0 3 0 5 2 7)
```

The **small-integer** function from the generators' namespace returns an integer between **-32768** and **32767**. The **sample**function returns a sample collection of the specified type.

With generator combinators, we can obtain new generators. The **fmap** generator allows us to create a new generator by applying a function to the values created by another generator:

```clojure
coffee-app.core=> (gen/sample (gen/fmap inc gen/small-integer))
(1 0 -1 4 4 -3 5 1 -6 5)
```

We were able to increase the numbers generated by the **small-integer** generator by applying the **inc** function using the **fmap** combinator.

A property is an actual test — it combines a generator with a function you want to test, and checks that the function behaves as expected given the generated values.

Properties are created using the **for-all** macro from the **clojure.test.check.properties** namespace:

```clojure
(ns coffee-app.utils-test
  (:require [clojure.test :refer [deftest testing are is]]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [coffee-app.core :refer [price-menu]]
            [coffee-app.utils :refer :all]))

(defspec coffee-price-test-check 1000
       (prop/for-all [int gen/nat]
                     (= (float (* int (:latte price-menu))) (calculate-coffee-price price-menu :latte int))))
```

```bash
➜ lein test
{:result true, :num-tests 1000, :seed 1601742185447, :time-elapsed-ms 26, :test-var "coffee-price-test-check"}

Ran 2 tests containing 2 assertions.
0 failures, 0 errors.

Ran 0 tests containing 0 assertions in 0 msecs
0 failures, 0 errors.
```

More in depth property test:

```clojure
(defspec coffee-price-test-check-all-params 1000
         (prop/for-all [int (gen/fmap inc gen/nat)
                        price-hash (gen/map gen/keyword
                                            (gen/double* {:min 0.1 :max 999 :infinite? false :NaN? false})
                                            {:min-elements 2})]

                       (let [coffee-tuple (first price-hash)]
                         (= (float (* int (second coffee-tuple)))
                            (calculate-coffee-price price-hash (first coffee-tuple) int)))))
```

## Testing in ClojureScript

In ClojureScript, we have a port of **clojure.test** in the form of **cljs.test**.

Regarding **asynchronous** code, ClojureScript sits on top of JavaScript which is single threaded and works with **callbacks**.

ClojureScript provides the **core.async** library for working with asynchronous code. The **core.async** library has a number of functions and macros:

- **go**: Creates a block that marks the code as asynchronous. The result from the block is put on a channel.
- **<!**: Takes a value from a channel.

Within this project, we have the sub-module [clojurescript-testing](../../../clojurescript-testing) where the project has the **dependencies**:

```clojure
:dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [cljs-http "0.1.46"]
                 [org.clojure/test.check "0.10.0"]
                 [funcool/cuerdas "2.2.0"]]
:plugins [[lein-doo "0.1.11"]]
```

The **cljs-http** library will allow us to make HTTP calls. We will use **GET** requests to make asynchronous calls that will be tested. The **cuerdas** library has many string utility functions.

The **lein-doo** plugin will be used to run ClojureScript tests.

While Maven hosts Java projects, npm hosts JavaScript projects. Install **Karma** with **npm**:

```bash
clojure-backwards/clojurescript-testing
➜ npm install karma karma-cljs-test --save-dev
```

Install the Chrome Karama launcher. Our tests will be run (launched) in the Chrome browser:

```bash
clojure-backwards/clojurescript-testing
➜ npm install karma-chrome-launcher --save-dev
```

Install the Karma command-line tool:

```bash
clojure-backwards/clojurescript-testing
➜ npm install -g karma-cli
```

We also set the build configuration for the test task in the **project.clj** file:

```clojure
:cljsbuild {:builds
              {:test {:source-paths ["src" "test"]
                      :compiler {:output-to "out/tests.js"
                                 :output-dir "out"
                                 :main clojurescript-testing.runner
                                 :optimizations :none}}}}
```

Launch the test runner:

```bash
lein doo chrome test
```

