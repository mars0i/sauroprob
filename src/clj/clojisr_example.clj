;; Shows that using `require-r`, names with dots in them get renamed
;; in Clojure, and you can load them using either name.
(ns clojisr-example
  (:require [clojisr.v1.r :as R]
            [fastmath.stats :as fs]))

;; Both the stats and dgof packages have functions named "ks.test"
;; We can load both using either "ks.test" or "ks-test".
(R/require-r '[stats :refer [ks.test]])
(R/require-r '[dgof :refer [ks-test]])

;; Calling the function as `ks.test` generates a "no such var" error,
;; but `ks-test` works:
(def came-from-dot
  (R/r->clj (r.stats/ks-test (range 42) (range 42))))

(def came-from-dash
  (R/r->clj (r.dgof/ks-test (range 42) (range 42))))

;; exact because sample size small:
(R/r->clj (r.stats/ks-test (range 30) (range 30)))
(R/r->clj (r.dgof/ks-test (range 30) (range 30)))
(fs/ks-test-two-samples (range 30) (range 30) {:method :exact})

;; exact because optional argument (note syntax):
(R/r->clj (r.stats/ks-test (range 1000) (range 1000) :exact [true])) ; <==
(R/r->clj (r.dgof/ks-test (range 1000) (range 1000)))
(fs/ks-test-two-samples (range 1000) (range 1000) {:method :exact})
