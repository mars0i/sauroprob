^:kindly/hide-code
(ns iterfreqs
  (:require ;[clojure.math :as m]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))

^:kindly/hide-code
;; Make LaTeX work in Plotly labels:
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

(let [f (um/normalized-ricker 2.70)
      comps [1 2 5]]
  (fns/three-plots {:x-max 2.5
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.90475
                    :n-iter-lines 10
                    :n-plot-iterates 200
                    :n-hist-iterates 1000000}))
