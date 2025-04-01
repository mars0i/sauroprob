

(require 'clojure.java.shell)
;(require 'clojure.repl)
;(require 'clojure.pprint)
;(clojure.repl/dir clojure.java.shell)

(print (:out (clojure.java.shell/sh "ls")))

(clojure.repl/pst)
