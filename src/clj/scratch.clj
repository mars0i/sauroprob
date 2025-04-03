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

(R/require-r '[stats :refer [ks.test]])
(R/require-r '[dgof :refer [ks.test]])


(def logisticvals1 (iterate um/logistic-4 0.14))
(def logisticvals2 (iterate um/logistic-4 0.16))
(def logisticvals3 (iterate um/logistic-4 0.41))
(def logisticvals2after180 (drop 180 logisticvals2))

(def ricker1 (iterate um/logistic-4 0.14))
(def ricker2 (iterate um/logistic-4 0.16))
(def ricker3 (iterate um/logistic-4 0.41))
(def ricker4 (iterate um/logistic-4 1.41))
(def ricker5 (iterate um/logistic-4 2.41))

(defn testem
  [xs ys]
  (let [rstats-result (R/r->clj (r.stats/ks-test xs ys))
        dgof-result (R/r->clj (r.dgof/ks-test xs ys))
        fastmath-result (fs/ks-test-two-samples xs ys)]
   ; {:rstats {:ks (:statistic rstats-result), :pval (:p.value rstats-result)}
   ;  :dgof {:ks (:statistic dgof-result), :pval (:p.value dgof-result)}
   ;  :fastmath {:ks (:stat fastmath-result)
   ;             :absdiff (:d fastmath-result)
   ;             :pval (:p-value fastmath-result)}}
   [rstats-result dgof-result fastmath-result]
    ))

(let [N 1000]
  (testem (take N logisticvals1)
          (take N logisticvals2after180)))
