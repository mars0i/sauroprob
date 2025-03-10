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
(let [catkey :fun
      x-max 1.0
      μ 3.75
      f (um/logistic μ)
      init-x 0.025
      iterates (iterate f init-x)
      n-iter-lines 20
      n-plot-iterates 400
      n-hist-iterates 30000
     ]
  (kind/fragment [
                  (kind/md ["Plot of the function itself, with parameter μ=" μ
                            ", with a few iterations starting from " init-x " shown:"])
                  (-> (tc/concat (sp/iter-lines init-x n-iter-lines catkey "iter" f)
                                 (tc/dataset {:x [0 x-max], :y [0 x-max], catkey "y=x"})
                                 (sp/fn2dataset [0 x-max] catkey "f" f)
                                 ;(sp/fn2dataset [0 x-max] catkey "f<sup>2</sup>" (msc/n-comp f 2))
                                 )
                      (plotly/base {:=height 400 :=width 450})
                      (plotly/layer-line {:=x :x, :=y, :y :=color catkey})
                      (sp/equalize-display-units) ; runs plotly/plot
                      (assoc-in [:data 0 :line :width] 1.5)
                      (assoc-in [:data 0 :line :dash] "dot"))

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
