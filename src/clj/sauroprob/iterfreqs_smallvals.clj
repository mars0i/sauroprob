;; See notes below on how small values get under iteration.
;; This is problematic for modeling populations.
;; I discuss this outside of this repo in my notes file
;; ~/prob/biol/biologicalPopulationPRNGsIterativeMapsNt9.md

^:kindly/hide-code
(ns iterfreqs-smallvals
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

(def logisticvals2after180 (drop 180 logisticvals2))

(def logisticvals2after180init (first logisticvals2after180))



(map #(ifn/plots-grid {:x-max 1.0 
                  :fs [um/logistic-4] 
                  :labels [(str "r=" %)]
                  :init-x %
                  :n-cobweb 14
                  :n-seq-iterates 300
                  :n-dist-iterates 10000})
     [0.14 0.41 0.16 logisticvals2after180init])

;; Look at that last sequence plot (upper right). The beginning goes:
^:kindly/hide-code
(take 12 logisticvals2after180)

;; i.e. after the 4th value, which is almost 1, it jumps down to almost zero,
;; where it stays for several steps.  You can see this in the cobweb diagram.

;; Look at how small the smallest value is: 1.25E-6, close to 1/million.  
;; So e.g. with clonal reproduction, you'd have to have a pop size of a million for 
;; that not to count as extinction.  
;; With sexual reproduction, the pop size would have to be at least two million.

(comment
;; When I compared 0.14 and 0.17 init values, 
;; the p-values below were all near 0.1, give or take, which is not very good.
;; The K-S statistic varies between about 1.8 and around 5.5.  What's that
;; about?  i.e. it's changing as I extend the very same sequence.

;; Below I compare 0.14 and 0.16.  Still large p-values, and fluctuating
;; K-S statistics as I extend the sequence:

(let [N 1000]
  (fs/ks-test-two-samples 
    (take N logisticvals1)
    (take N logisticvals2)))

(let [N 10000]
  (fs/ks-test-two-samples 
    (take N logisticvals1)
    (take N logisticvals2)))

(let [N 100000]
  (fs/ks-test-two-samples 
    (take N logisticvals1)
    (take N logisticvals2)))

(let [N 1000000]
  (fs/ks-test-two-samples 
    (take N logisticvals1)
    (take N logisticvals2)))
)


