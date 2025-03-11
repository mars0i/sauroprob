^:kindly/hide-code
(ns iterfreqs
  (:require ;[clojure.math :as m]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]))

^:kindly/hide-code
;; Make LaTeX work in Plotly labels:
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

^:kindly/hide-code
;; Note the use of \text{} to display text on the same line as LaTeX without using Quarto:

;; $\text{Logistic map with parameter }\mu: f(x) = \mu x (1 - x)$

^:kindly/hide-code
(let [catkey :fun
      x-max 1.0
      μ 3.99
      f (um/logistic μ)
      init-x 0.3475
      iterates (iterate f init-x)
      n-iter-lines 60
      n-plot-iterates 400
      n-hist-iterates 30000
      ]
  (kind/fragment [
                  (kind/md ["Plot of the function itself, with parameter μ=" μ
                            ", with a few iterations starting from " init-x ":"])
                  (-> (tc/concat (sp/iter-lines init-x n-iter-lines catkey "iterative mappings" f)
                                 (tc/dataset {:x [0 x-max], :y [0 x-max], catkey "$y=x$"})
                                 (sp/fn2dataset [0 x-max] catkey "$f(x)=\\mu x(1-x)$" f)
                                 (sp/fn2dataset [0 x-max] catkey "$f^2$" (msc/n-comp f 2)))
                      (plotly/base {:=height 400 :=width 550})
                      (plotly/layer-line {:=x :x, :=y, :y :=color catkey})
                      (sp/equalize-display-units) ; runs plotly/plot
                      (sp/set-line-width 0 1.5)
                      (sp/set-line-dash 0 "dot")
                      (sp/set-line-dash 1 "dash"))

                  (kind/md ["Plot of a sequence of values of the function beginning from "
                            init-x ":"])
                  (-> (tc/dataset {:x (range n-plot-iterates)
                                   :y (take n-plot-iterates iterates)})
                      (plotly/base {:=height 400 :=width 800})
                      (plotly/layer-line {:=x :x, :=y, :y})
                      plotly/plot
                      (assoc-in [:data 0 :line :width] 1))

                  (kind/md ["Distribution of values beginning from " init-x ":"])
                  (-> (tc/dataset {:x (take n-hist-iterates iterates)})
                      (plotly/base {:=height 400 :=width 800})
                      (plotly/layer-histogram {:=x :x
                                               :=histogram-nbins 200}))
                  ]))
