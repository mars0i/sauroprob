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

(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

fs/ks-test-two-samples

(def logisticvals1 (iterate um/logistic-4 0.14))
(def logisticvals2 (iterate um/logistic-4 0.17))


(map #(ifn/plots-grid {:x-max 1.0 
                  :fs [um/logistic-4] 
                  :labels [(str "r=" %)]
                  :init-x %
                  :n-cobweb 14
                  :n-seq-iterates 300
                  :n-dist-iterates 1000})
     [0.14 0.16 0.41])
