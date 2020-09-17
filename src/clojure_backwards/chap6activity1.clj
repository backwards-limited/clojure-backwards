(ns clojure-backwards.chap6activity1
  (:require
    [clojure.pprint :as pp]
    [clojure.string :as string]))

(defn attributes [m]
  (string/join " "
               (->> m
                    (map (fn [[k, v]]
                           (str (name k) (if (string? v) (str "=\"" v "\""))))))))

(println (attributes {:id "page-title" :class "main-component" :checked true}))

(defn keyword->opening-tag [kw]
  (str "<" (name kw) ">"))

(defn keyword-attributes->opening-tag [kw attrs]
  (str "<" (name kw) " " (attributes attrs) ">"))

(defn keyword->closing-tag [kw]
  (str "</" (name kw) ">"))

(defn has-attributes? [tree]
  (map? (second tree)))

(defn singleton? [tree]
  (and (vector? tree)
       (#{:img :meta :link :input :br} (first tree))))

(defn singleton-with-attrs? [tree]
  (and (singleton? tree) (has-attributes? tree)))

(defn element-with-attrs? [tree]
  (and (vector? tree) (has-attributes? tree)))

(defn html [tree]
  (cond
    (not tree) tree

    (string? tree) tree

    (singleton-with-attrs? tree) (keyword-attributes->opening-tag (first tree) (second tree))

    (singleton? tree) (keyword->opening-tag (first tree))

    (element-with-attrs? tree) (apply str
                                      (concat
                                        [(keyword-attributes->opening-tag (first tree) (second tree))]
                                        (map html (next (next tree)))
                                        [(keyword->closing-tag (first tree))]))

    (vector? tree) (apply str
                          (concat
                            [(keyword->opening-tag (first tree))]
                            (map html (next tree))
                            [(keyword->closing-tag (first tree))]))))

(html
  [:html
   [:head [:title "HTML output from vectors"]]
   [:body
    [:h1 {:id "page-title"} "HTML out from vectors"]
    [:div {:class "main-component"}
     [:p "Converting nested lists into HTML is an old Lisp trick"]
     [:p "But Clojure uses Vectors instead"]]]])

(def html-string (html
  [:html
   [:head [:title "HTML output from vectors"]]
   [:body
    [:h1 {:id "page-title"} "HTML out from vectors"]
    [:div {:class "main-component"}
     [:p "Converting nested lists into HTML is an old Lisp trick"]
     [:p "But Clojure uses Vectors instead"]]]]))

(println html-string)