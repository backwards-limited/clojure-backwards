(ns clojure-backwards.chap9
  (:import
    (java.math BigDecimal BigInteger)
    (java.time LocalTime)
    (java.util Locale ArrayList)))

; In Java, we construct an instance of a class using the new keyword:
; BigDecimal big_number = new BigDecimal("100000");

; In Clojure call the "new" function on the BigDecimal class to create an instance of it:
(println (new BigDecimal "100000"))
; OR
(def big-num (BigDecimal. "100000"))

(println (Locale. "pl"))

; What about Java static methods?
; LocalTime time = LocalTime.now();

; We can also access static methods from the LocalTime class in Clojure. This is done by stating the class followed by a slash and a method name.
(println (LocalTime/now))

; Accessing instance methods, we use the dot operator with a function name:
(println (.negate big-num))

; This was an example of calling a function without any arguments. In the following example, we will see how to call an instance method that accepts arguments.
; In Java:
; BigDecimal big_num = new BigDecimal("100000");
; big_num.pow(2);
(.pow big-num 2)

; Java Data types
(def capitals ["Berlin" "Oslo" "Warszawa" "Belgrad"])
(println (class capitals))

; Using Clojure's vector, we can create an ArrayList in Java:
(def destinations (ArrayList. capitals))
; We created an ArrayList from a vector.

; We can also convert the other way - We can convert from Java to Clojure as follows:
(vec destinations)

(def fluss {"Germany" "Rhein" "Poland" "Vistula"})

(def rivers (java.util.HashMap. fluss))

; Using HashMap from Java, we can create a hash in Clojure:
(into {} rivers)


; Handling Errors and Exceptions in Clojure
(def three-numbers-array (ArrayList. [0 1 2]))

(defn array-list-getter [array index]
  (try
    (.get array index)
    (catch IndexOutOfBoundsException ex
      (str "No element at index " index ", because of, " (.getMessage ex)))
    (finally
      (println "Done with array-list-getter"))))

(println (array-list-getter three-numbers-array 5))