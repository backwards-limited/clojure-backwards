# Leiningen

```bash
➜ lein new app hello-leiningen
Generating a project called hello-leiningen based on the 'app' template.
```

**new**: A **lein** task telling Leiningen what type of task to execute. Task **new** will create a project based on a template.

**app**: The name of the template to use when creating a project. Leiningen will create a project using a specified template.

**hello-leiningen**: The name of the project.

```bash
➜ tree hello-leiningen
hello-leiningen
├── CHANGELOG.md
├── LICENSE
├── README.md
├── doc
│   └── intro.md
├── project.clj
├── resources
├── src
│   └── hello_leiningen
│       └── core.clj
└── test
    └── hello_leiningen
        └── core_test.clj
```

```bash
➜ bat project.clj
   1   │ (defproject hello-leiningen "0.1.0-SNAPSHOT"
   2   │   :description "FIXME: write description"
   3   │   :url "http://example.com/FIXME"
   4   │   :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
   5   │             :url "https://www.eclipse.org/legal/epl-2.0/"}
   6   │   :dependencies [[org.clojure/clojure "1.10.1"]]
   7   │   :main ^:skip-aot hello-leiningen.core
   8   │   :target-path "target/%s"
   9   │   :profiles {:uberjar {:aot :all
  10   │                        :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
```

- **Main namespace**: In the **:main** keyword, we specify the namespace that is the entry point for our project and application. It gets called when we run our project.

- **Ahead of time** (**AOT**): Clojure compiles all our code on the fly into JVM bytecode. AOT compilation allows us to compile code before we run it. This speeds up application startup. We can see that under the **:profiles** keyword, we have an **uberjar** profile where we want AOT compilation. On the other hand, we would like our **:main** namespace to be without AOT. We create an uberjar with AOT because we want to compile the code before we run it. We do not want AOT for **:main** as we want to defer compilation until we start an application. For example, **:main** can use symbols such as environment settings, parameters that are not available at AOT compilation. They are only available when we start an application. If we compile too fast, the application will not have access to parameters that we passed when we start an application.

- **Profiles**: Leiningen allows us to set up various profiles in our projects. Thanks to profiles, we can customize projects depending on our needs.

  For example, a development version could require a testing suite and we might want testing dependencies. On the other hand, when creating a production jar, we do not need testing dependencies.

```clojure
:main ^:skip-aot hello-leiningen.core
```

**^:skip-aot** instructs Leiningen to skip AOT for the namespace that we specify. Here, the namespace is **hello-leiningen.core**.

## Run

After creating a new project, the content of the **hello-leiningen.core** namespace is the following:

```clojure
(ns hello-leiningen.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
```

**(:gen-class)** instructs Leiningen to generate a Java class from the namespace. Build tools such as Leiningen execute Java bytecode so we need to compile Clojure to bytecode in order to run the **core** namespace.

Next, we have the **-main** function. By default, when an application is started, Leiningen will search for a method with that name and execute it. As such, **-main** is an entry point to our application.

To run the application from the command line, we use Leiningen's **run** task:

```bash
➜ lein run
Hello, World!
```

## Run with command line args

```clojure
(ns hello-leiningen.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> (str/join " " args)
      (str/replace "melon" "banana")
      (str/replace "apple" "orange")
      println))
```

```bash
➜ lein run "apple" "melon" "grapes"
orange banana grapes
```

## Creating and Executing a *jar* with Leiningen

Leiningen provides two tasks that can create a jar:

- jar
- uberjar

The difference is that a jar task will package only our code while an uberjar task will also package dependencies.

The **:gen-class** directive is an important concept in Clojure. This directive will generate a Java class corresponding to the target namespace. The result of generating a Java class is a **.class**file. A Java **.class** file contains Java bytecode that can be executed on the JVM. Such a file can be executed by build tools such as Leiningen.

```bash
➜ lein uberjar
```

This task will create **hello-leiningen-0.1.0-SNAPSHOT.jar** and **hello-leiningen-0.1.0-SNAPSHOT-standalone.jar** jar files inside the target directory.

```bash
➜ java -jar target/uberjar/hello-leiningen-0.1.0-SNAPSHOT-standalone.jar 
```

## Leiningen Profiles

Profiles are a Leiningen tool that allows us to change the configuration of our projects. A profile is a specification that influences how a project behaves. For example, during development or testing, say that we would like to include testing frameworks in our builds but the production build does not need testing dependencies

Leiningen allows us to define profiles in a few places depending on our needs:

- In the **project.clj** file
- In the **profiles.clj** file
- In the **~/.lein/profiles.clj** file

The difference between putting a profile in **project.clj** and **profiles.clj** is that profiles in **project.clj** will be committed in version control. Profiles defined in **profiles.clj** are independent of the project configuration in **project.clj** and do not need to be committed to version control. Profiles from both files are merged together by Leiningen. Profiles with the same name in **profiles.clj** take precedence over profiles in **project.clj**.

E.g. adding a **:dev** profile in **project.clj**:

```clojure
:profiles {:uberjar {:aot :all
                     :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
           :dev {:dependencies [[expectations "2.1.10"]]}}
```

```bash
➜ lein show-profiles
base
debug
default
dev
leiningen/default
leiningen/test
offline
uberjar
update

```

If we wanted to run this (dev) profile, we would call the **with-profiles** task:

```bash
lein with-profile dev test
```

Calling this task would run the tests with the **dev** profile.

## User-Wide Profile

Using any editor:

```bash
➜ code ~/.lein/profiles.clj
```

```clojure
{
  :user {
    :plugins [
      [lein-localrepo/lein-localrepo "0.5.4"]
      [venantius/ultra "0.6.0"]
      [lein-ancient "0.6.15"]
    ] 
    :dependencies [
      [clojure-humanize/clojure-humanize "0.2.2"]
    ]
  }
}
```

```bash
➜ lein repl
Retrieving lein-localrepo/lein-localrepo/0.5.4/lein-localrepo-0.5.4.pom from clojars
Retrieving org/clojure/tools.cli/0.3.5/tools.cli-0.3.5.pom from central
Retrieving venantius/ultra/0.6.0/ultra-0.6.0.pom from clojars
...

hello-leiningen.core=> (require 'clojure.contrib.humanize)
nil

hello-leiningen.core=> (clojure.contrib.humanize/numberword 4589)
"four thousand five hundred and eighty-nine"
```

As **profiles.clj** is shared globally we can (with the plugins we have) check a project's dependencies are up to date, and of course can keep up to date across all projects with dependencies set in **profiles.clj**:

```bash
➜ lein ancient
...
all artifacts are up-to-date.
```

