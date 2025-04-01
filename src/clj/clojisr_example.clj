;; Shows that using `require-r`, names with dots in them get renamed
;; in Clojure, and you can load them using either name.
(ns clojisr-example
  (:require [clojisr.v1.r :as r]))

;; Both the stats and dgof packages have functions named "ks.test"
;; We can load both using either "ks.test" or "ks-test".
(r/require-r '[stats :refer [ks.test]])
(r/require-r '[dgof :refer [ks-test]])

;; Calling the function as `ks.test` generates a "no such var" error,
;; but `ks-test` works:
(def came-from-dot
  (r/r->clj (r.stats/ks-test (range 42) (range 42))))

(def came-from-dash
  (r/r->clj (r.dgof/ks-test (range 42) (range 42))))

