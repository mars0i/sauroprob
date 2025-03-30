^:kindly/hide-code
;; (Based on https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/.E2.9C.94.20LaTeX.20.20in.20TMD.20fields.20into.20Plotly.20legends.3F/near/504227110)

^:kindly/hide-code
(ns latexinplotly
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

(defn dist-and-pval
  [& {:keys [:d :p-value]}]
  {:d d, :p-value p-value})

(defn my-ks-test
  [xs1 xs2]
  (dist-and-pval
    (fs/ks-test-two-samples xs1 xs2)))

(let [N 1000]
  (dist-and-pvalue
  (fs/ks-test-two-samples 
    (take N logisticvals1)
    (take N logisticvals2))))

(let [N 10000]
  (fs/ks-test-two-samples 
    (take N logisticvals1)
    (take N logisticvals2)))

(let [N 100000]
  (fs/ks-test-two-samples 
    (take N logisticvals1)
    (take N logisticvals2)))

(comment
(let [N 1000000]
  (fs/ks-test-two-samples 
    (take N logisticvals1)
    (take N logisticvals2)))
)


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
