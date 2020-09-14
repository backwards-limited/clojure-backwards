# REPL

You have a clojure file in a Leiningen project e.g.

```bash
src/clojure_backwards/myfile.clj
```

Boot a REPL and load:

```bash
clj

user=> (load "clojure_backwards/myfile")

user=> (in-ns 'clojure-backwards.myfile)
```

