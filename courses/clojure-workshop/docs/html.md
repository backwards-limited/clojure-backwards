# HTML

In Clojure, vectors are generally used for representing the structure of an HTML document. One of the more popular libraries that does this is called **Hiccup**. With **Hiccup**, a short paragraph containing a link would look like this:

```clojure
[:p "This paragraph is just an " [:a {:href "http://example.com"} "example"] "."]
```

The output would be as follows:

```html
<p>This paragraph is just an <a href="http://example.com">example</a>.</p>
```

Besides using vectors, it uses keywords to identify HTML tags and maps to add attributes such as **href** or **class**.

## Generating HTML from Clojure Vectors

We are going to write your own system for generating HTML from nested vectors, using this format. The goal is to be able to take any vector written with this syntax, including an arbitrary number of descendant vectors, and produce a single string containing correctly structured HTML.

Let's generate HTML for say:

```clojure
(html
  [:html
   [:head [:title "HTML output from vectors"]]
   [:body
    [:h1 {:id "page-title"} "HTML out from vectors"]
    [:div {:class "main-component"}
     [:p "Converting nested lists into HTML is an old Lisp trick"]
     [:p "But Clojure uses Vectors instead"]]]])
```

```clojure
(defn attributes [m]
  (string/join " "
               (->> m
                    (map (fn [[k, v]]
                           (str (name k) (if (string? v) (str "=\"" v "\""))))))))
```

```clojure
(defn keyword->opening-tag [kw]
  (str "<" (name kw) ">"))

(defn keyword-attributes->opening-tag [kw attrs]
  (str "<" (name kw) " " (attributes attrs) ">"))

(defn keyword->closing-tag [kw]
  (str "</" (name kw) ">"))
```

We need to be able to distinguish between input vectors that have attributes and those that don't. We do this by looking at the type of the second item in the vector:

```clojure
(defn has-attributes? [tree]
  (map? (second tree)))
```

```clojure
(defn singleton? [tree]
  (and (vector? tree)
       (#{:img :meta :link :input :br} (first tree))))

(defn singleton-with-attrs? [tree]
  (and (singleton? tree) (has-attributes? tree)))

(defn element-with-attrs? [tree]
  (and (vector? tree) (has-attributes? tree)))
```

Finally:

```clojure
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
```

