(defproject sauroprob "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/math.numeric-tower "0.0.5"]
                 [org.clojure/data.json "2.4.0"]
                 [generateme/fastmath "2.1.8"] ; or 2.1.9-SNAPSHOT
                 [aerial.hanami "0.17.0"]
                 [techascent/tech.viz "6.00-beta-16-4"]
                 ;[io.github.nextjournal/clerk "0.5.346"]
                 ;[metasoarous/oz "2.0.0-alpha5"]
                 [metasoarous/oz "1.6.0-alpha36"] ; As of 1/12/2023 site says "Please use 1.6.0-alpha36 for the most recent stable version of Oz. "
                 [cljplot "0.0.2a-SNAPSHOT"]
                ]
  :source-paths ["src/clj"]
)

 ;; :repl-options {:init-ns sauroprob.core})
