^:kindly/hide-code
(ns scratch
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            ;[fastmath.random :as fr]
            [fastmath.stats :as fs]
            [tablecloth.api :as tc]
            [clojisr.v1.r :as R]
            [criterium.core :as crit]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as ifn]))

;; See clojisr-example for tips e.g. syntax for optional args in R.

(R/require-r '[stats :refer [ks.test]])
(R/require-r '[dgof :refer [ks.test]])

(defn fs-ks-test
  "Returns a map with the difference :d and the p-value :p of comparison of
  sequences with initial value :x and with initial value :y.  Uses
  fastmath's ks-test-two-samples."
  [xs ys]
  (let [result (fs/ks-test-two-samples xs ys {:method :exact})]
    {:x (first xs), :y (first ys),
     :d (:d result), :p (:p-value result)}))
        ;; change this for the R versions

(defn rst-ks-test
  "Returns a map with the difference :d and the p-value :p of comparison of
  sequences with initial value :x and with initial value :y.  Uses R's
  dgof/ks.test."
  [xs ys]
  (let [result (R/r->clj (r.stats/ks-test xs ys :exact true))]
    {:x (first xs), :y (first ys),
     :d (first (:statistic result)), :p (first (:p.value result))}))

(defn rdg-ks-test
  "Returns a map with the difference :d and the p-value :p of comparison of
  sequences with initial value :x and with initial value :y.  Uses R's
  dgof/ks.test."
  [xs ys]
  (let [result (R/r->clj (r.dgof/ks-test xs ys :exact true))]
    {:x (first xs), :y (first ys),
     :d (first (:statistic result)), :p (first (:p.value result))}))

(def rick (um/normalized-ricker 3.0))

(defn rick-iters 
  [n-iters init]
  (take n-iters (iterate rick init)))

(def inits (range 0.01 3.0 0.01))
(def n-iters 1000)

(comment
  ;; Perform ks-test of the first distribution with all of the others:

  (def first-iters (doall (rick-iters n-iters (first inits))))

  ;; Remember that map and iterate are lazy, so without doall,
  ;; these might return immediately without having done any work:
  (crit/bench
    (do
      (doall (map (comp (partial fs-ks-test first-iters) 
                        (partial rick-iters n-iters)) 
                  (rest inits)))
      )
   )

  (crit/bench
    (do
      (def other-iters (map (partial rick-iters n-iters) (rest inits)))
      (def rdg-checks (doall (map (partial rdg-ks-test first-iters) other-iters)))
      )
    )

  (crit/bench
    (do
      (def other-iters (map (partial rick-iters n-iters) (rest inits)))
      (def rs-checks (doall (map (partial rst-ks-test first-iters) other-iters)))
      )
    )

  ;; none of the p-values indicate that the distributions are different:
  (def min-pval (apply min (map :p ks-checks)))
  ;; But the distances are not very close:
  (def max-diff (apply max (map :d ks-checks)))
)
