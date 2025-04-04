^:kindly/hide-code
(ns scratch
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            ;[fastmath.random :as fr]
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
(def yo (iterate um/logistic-4 0.3085937081153858))

(first logisticvals2)
(first logisticvals2after180)
(first yo)
(= (take 1000 logisticvals2after180) (take 1000 yo))

(def ricker1 (iterate um/logistic-4 0.14))
(def ricker2 (iterate um/logistic-4 0.16))
(def ricker3 (iterate um/logistic-4 0.41))
(def ricker4 (iterate um/logistic-4 1.41))
(def ricker5 (iterate um/logistic-4 2.41))

(defn testem
  [xs ys & {:keys [exact]}]
  (let [rstats-result (R/r->clj (r.stats/ks-test xs ys :exact exact))
        dgof-result (R/r->clj (r.dgof/ks-test xs ys :exact exact))
        fastmath-result (fs/ks-test-two-samples xs ys {:method
                                                       (if exact
                                                        :exact
                                                        :approximate)})]
    (prn :exact exact)
    {:rstats rstats-result 
     :dgof dgof-result 
     :fastmath fastmath-result}))

(comment
(let [N 600]
  (testem  (take N logisticvals1)
           (take N logisticvals2after180)
          :exact true  ;; NOTE Fastmath's returning ##NaN as p-value with :exact true
          ))

(let [N 10000]
  (testem  (range N)
           (map #(+ % 0.5) (range N))
          :exact true))

)

    ;{:rstats {:ks (:statistic rstats-result), :pval (:p.value rstats-result)}
    ; :dgof {:ks (:statistic dgof-result), :pval (:p.value dgof-result)}
    ; :fastmath {:ks (:stat fastmath-result)
    ;            :absdiff (:d fastmath-result)
    ;            :pval (:p-value fastmath-result)}}

;; dgof ks.test help says (paragraph 5 of "Details"):

; If exact = NULL (the default), an exact p-value is computed if
; the sample size is less than 100 in the one-sample case with y
; continuous or if the sample size is less than or equal to 30 with
; y discrete; or if the product of the sample sizes is less than
; 10000 in the two-sample case for continuous y. Otherwise,
; asymptotic distributions are used whose approximations may be
;inaccurate in small samples.

; In other words (?), for my two-sample non-continuous tests, dgof/ks.test
; uses exact iff the sample size is <= 30?  Note that in that case the
; output includes what appears to be the actual data.  Well stats/ks.test
; does that last thing, too.  So in any even dgof doesn't tell you whether
; it was exact or not.  You just have to know.

