^:kindly/hide-code
;; (Based on https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/.E2.9C.94.20LaTeX.20.20in.20TMD.20fields.20into.20Plotly.20legends.3F/near/504227110)

^:kindly/hide-code
(ns scratch-kstest
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [fastmath.random :as fr]
            [fastmath.stats :as fs]
            [tablecloth.api :as tc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as ifn]))


;; Make LaTeX work in Plotly labels:
;(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])
;; I'm stopping using this because when labels are long, the Plotly
;; legend interferes with the plot.  See mwe5.clj for details.

;; ## Exploring K-S tests on iteration-generated values

(def logisticvals1 (iterate um/logistic-4 0.14))
(def logisticvals2 (iterate um/logistic-4 0.16))
(def logisticvals3 (iterate um/logistic-4 0.41))
(def logisticvals4 (drop 180 logisticvals2)) ; The last sequence starts at a small value.

;; From most recent version of fastmath (since current release 3.0.0-alpha3):
;; ks-test-two-samples returns:
;;   A map with the following keys:
;;   - `:n`: Effective sample size.
;; [Note: in the examples below, this seems to be half the size of both pops.]
;;   - `:nx`: Number of observations in `xs`.
;;   - `:ny`: Number of observations in `ys`.
;;   - `:dp`: Maximum positive difference between empirical cumulative distribution functions.
;;   - `:dn`: Maximum negative difference between empirical cumulative distribution functions.
;;   - `:d`: The maximum absolute difference between ECDFs.
;;   - `:stat`: KS statistic, scaled `d` for asymptotic method.
;;   - `:KS`: Alias for `:stat`.
;;   - `:sides`: The alternative hypothesis used.
;;   - `:p-value`: The computed p-value indicating the significance of the test.

(defn ks-vals
  [& {:keys [:d :p-value :stat]}]
  {:d d, :p-value p-value :stat stat})

(defn my-ks-test
  [xs1 xs2]
  (ks-vals (fs/ks-test-two-samples xs1 xs2)))

;; (in)sanity check:
;; In an answer on stats.SE (https://stats.stackexchange.com/a/48601/19960),
;; Greg Snow say that "the null hypothesis is that the distributions are
;; identical".  So a small p-val means if the generating dists are the same, 
;; it's unlikely that the data would be as different as it is.
;; outcome would be very unlikely under the null hypothesis." (Wikipedia)
;; So a small p-value means that we can reject the null, because the data
;; would be very unlikely were the null true.
;;
;; Similarly, the help for ks.test in the R package dgof ["discrete
;; goodness of fit"] says that the p-value "If y is numeric, a two-sample
;; test of the null hypothesis that x and y were drawn from the same
;; *continuous* distribution is performed", and concerns "the null
;; hypothesis that the true distribution function of x is equal to ...
;; the distribution function of y (two-sample case)".
;;
;; So a p-value greater than 0.05 or whatever alpha you choose, means that
;; we can't reject the null, i.e. they might be the output of the same
;; distribution.

;; Example:  This gives a p-value of 1, so we can't reject the null.  Good.
(fs/ks-test-two-samples (range 1000) (range 1000))

;; Below, the p-values range from 0.15 to 0.92, so are consistent
;; with being generated by the same distributions.  As we increase N,
;; the distances :d get closer.

(let [N 100]
  (my-ks-test 
    (take N logisticvals1)
    (take N logisticvals2)))
^:kindly/hide-code
; =>
; {:d 0.16, :p-value 0.15453805538450638, :stat 1.131370849898476}

(let [N 1000]
  (my-ks-test 
    (take N logisticvals1)
    (take N logisticvals2)))
^:kindly/hide-code
; =>
; {:d 0.04900000000000004,
;  :p-value 0.18111964545948345,
;  :stat 1.0956733089748978}

(let [N 10000]
  (my-ks-test 
    (take N logisticvals1)
    (take N logisticvals2)))
^:kindly/hide-code
; =>
; {:d 0.008800000000000002,
;  :p-value 0.8335174206375168,
;  :stat 0.6222539674441621}

(let [N 100000]
  (my-ks-test 
    (take N logisticvals1)
    (take N logisticvals2))) 
^:kindly/hide-code
; =>
; {:d 0.0024500000000000056,
;  :p-value 0.924972889869936,
;  :stat 0.5478366544874497}

(comment
  ;; takes a long time
(let [N 1000000]
  (my-ks-test 
    (take N logisticvals1)
    (take N logisticvals2)))
; ^:kindly/hide-code
; =>
; {:d 0.001263999999999993,
;  :p-value 0.4013717039125356,
;  :stat 0.8937829714197911}

)

;; --------------------------
;; Compare other sequences:

(let [N 100000]
  (my-ks-test 
    (take N logisticvals1)
    (take N logisticvals3))) 
^:kindly/hide-code
; {:d 0.0025200000000000057,
;  :p-value 0.9086338760487859,
;  :stat 0.5634891303299483}

(let [N 100000]
  (my-ks-test 
    (take N logisticvals2)
    (take N logisticvals3))) 
^:kindly/hide-code
; {:d 0.002250000000000005,
;  :p-value 0.9619176463457608,
;  :stat 0.5031152949374538}

(let [N 100000]
  (my-ks-test 
    (take N logisticvals1)
    (take N logisticvals3))) 
^:kindly/hide-code
; {:d 0.0025200000000000057,
;  :p-value 0.9086338760487859,
;  :stat 0.5634891303299483}

(let [N 100000]
  (my-ks-test 
    (take N logisticvals2)
    (take N logisticvals3))) 
^:kindly/hide-code
; {:d 0.002250000000000005,
;  :p-value 0.9619176463457608,
;  :stat 0.5031152949374538}

(let [N 100000]
  (my-ks-test 
    (take N logisticvals3)
    (take N logisticvals4))) 
^:kindly/hide-code
; {:d 0.002270000000000005,
;  :p-value 0.9588836280837709,
;  :stat 0.5075874308924534}

;; OK, now let's break it by choosing a fixed point:
(let [N 100000]
  (my-ks-test 
    (take N logisticvals3)
    (take N (iterate um/logistic-4 0.75)))) ; returns 0.75, 0.75, 0.75, ...
^:kindly/hide-code
; {:d 0.6672299999995982,
;  :p-value 0.76486718255575,
;  :stat 0.6672266638746192}
;; 
;; Wow, that's unexpected.  Not sure K-S test is what I want here.
;; Although the distances :d *is* large.
;;
;; Well, maybe all I really want is that the max discrepancy is small.
;; And in this case it's not.
;; The p-value is kind of just a flourish--unless it's small.



;; --------------------------
;; A quick test of some Ricker seqs:

(let [N 100000]
  (my-ks-test 
    (take N (iterate (um/normalized-ricker 3.0) 0.16))
    (take N (iterate (um/normalized-ricker 3.0) 0.965))))
^:kindly/hide-code
; {:d 0.0032800000000000077,
;  :p-value 0.6551009372314645,
;  :stat 0.7334302966199328}



(comment
(map #(ifn/plots-grid {:x-max 1.0 
                  :fs [um/logistic-4] 
                  :labels [(str "r=" %)]
                  :init-x %
                  :n-cobweb 14
                  :n-seq-iterates 300
                  :n-dist-iterates 10000})
     [0.14 0.41 0.16 logisticvals2after180init])
)
