^:kindly/hide-code
(ns scratch
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [fastmath.random :as fr]
            [fastmath.stats :as fs]
            [tablecloth.api :as tc]
            [clojisr.v1.r :as R]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as ifn]))

;; See clojisr-example for tips e.g. syntax for optional args in R.

^:kindly/hide-code
(R/require-r '[stats :refer [ks.test]])
^:kindly/hide-code
(R/require-r '[dgof :refer [ks.test]])

;; exact because sample size small:
(R/r->clj (r.stats/ks-test (range 30) (range 30)))
(R/r->clj (r.dgof/ks-test (range 30) (range 30)))
(fs/ks-test-two-samples (range 30) (range 30))

(R/r->clj (r.stats/ks-test (range 100) (range 100)))
(R/r->clj (r.dgof/ks-test (range 100) (range 100)))
(fs/ks-test-two-samples (range 100) (range 100))

(R/r->clj (r.stats/ks-test (range 1000) (range 1000)))
(R/r->clj (r.dgof/ks-test (range 1000) (range 1000)))
(fs/ks-test-two-samples (range 1000) (range 1000))

(R/r->clj (r.stats/ks-test (range 100000) (range 100000)))
(R/r->clj (r.dgof/ks-test (range 100000) (range 100000)))
(fs/ks-test-two-samples (range 100000) (range 100000))
