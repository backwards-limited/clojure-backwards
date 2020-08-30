(ns clojure-backwards.chap2)

(require '[clojure.pprint :as pp])

(defn encode-letter
  [s x]
  (println (str "Encode letter: " s))
  (let [code (Math/pow (+ x (int (first (char-array s)))) 2)]
    (str "#" (int code))))

(defn encode
  [s]
  (let [number-of-words (count (clojure.string/split s #" "))]
    (clojure.string/replace s #"\w" (fn [s] (encode-letter s number-of-words)))))

(println (encode "Hello World"))

(println (encode "Super secret"))

(defn decode-letter
  [x y]
  (let [number (Integer/parseInt (subs x 1))
        letter (char (- (Math/sqrt number) y))]
    (str letter)))

(defn decode [s]
  (let [number-of-words (count (clojure.string/split s #" "))]
    (clojure.string/replace s #"\#\d+" (fn [s] (decode-letter s number-of-words)))))

(println (decode-letter "#7225" 2))

(def gemstone-db {
  :ruby {
    :name "Ruby"
    :stock 120
    :sales [1990 3644 6376 4918 7882 6747 7495 8573 5097 1712]
    :properties {
      :dispersion 0.018
      :hardness 9.0
      :refractive-index [1.77 1.78]
      :color "Red"
      }
  }
  :diamond {
    :name "Diamond"
    :stock 10
    :sales [8295 329 5960 6118 4189 3436 9833 8870 9700 7182 7061 1579]
    :properties {
      :dispersion 0.044
      :hardness 10
      :refractive-index [2.417 2.419]
      :color "Typically yellow, brown or gray to colorless"
    }
  }
  :moissanite {
    :name "Moissanite"
    :stock 45
    :sales [7761 3220]
    :properties {
      :dispersion 0.104
      :hardness 9.5
      :refractive-index [2.65 2.69]
      :color "Colorless, green, yellow"
    }
  }
})

(println (get (get (get gemstone-db :ruby) :properties) :hardness))

(println (:hardness (:properties (:ruby gemstone-db))))

(println (get-in gemstone-db [:ruby :properties :hardness]))

(defn durability
  [db gemstone]
  (get-in db [gemstone :properties :hardness]))

(def updated-gemstone-db (assoc-in gemstone-db [:ruby :properties :color] "Near colorless through pink through all shades of red to a deep crimson"))

(pp/pprint updated-gemstone-db)

(defn change-color
  [db gemstone new-color]
  (assoc-in db [gemstone :properties :color] new-color))

(pp/pprint (update-in gemstone-db [:diamond :stock] dec))

(defn sell
  [db gemstone client-id]
  (let [clients-updated-db (update-in db [gemstone :sales] conj client-id)]
    (update-in clients-updated-db [gemstone :stock] dec)))

; In memory database using atom

(def memory-db (atom {}))

(defn read-db [] @memory-db)

(defn write-db [new-db] (reset! memory-db new-db))

{
  :table1 {
    :data []
    :indexes {}
  }
  :table2 {
    :data []
    :indexes {}
  }
}

; Example of data in database
{
  :clients {
    :data [{:id 1 :name "Bob" :age 30} {:id 2 :name "Alice" :age 24}]
    :indexes {:id {1 0, 2 1}}
  },
  :fruits {
    :data [{:name "Lemon" :stock 10} {:name "Coconut" :stock 3}]
    :indexes {:name {"Lemon" 0, "Coconut" 1}}
  },
  :purchases {
    :data [{:id 1 :user-id 1 :item "Coconut"} {:id 1 :user-id 2 :item "Lemon"}]
    :indexes {:id {1 0, 2 1}}
  }
}

(defn create-table
  [table-name]
  (write-db (assoc (read-db) table-name {:data [] :indexes {}})))

(println (create-table "fruits"))