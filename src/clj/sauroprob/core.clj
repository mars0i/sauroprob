^:kindly/hide-code
(ns sauroprob.core
  (:require [clojure.math :as m] ; new in Clojure 1.11 
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.clay.v2.api :as clay] ; needed for clay eval keymappings
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.hanami :as sh]
            [sauroprob.plotly :as sp]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; ## Plotly examples

(let [μ 4, f (um/logistic μ)]
  (->
    (tc/concat
      (tc/dataset {:x [0 1], :y [0 1], :fun "y=x"})
      (sp/fn2dataset [0 1] :fun "f" f)
      (sp/iter-lines 0.05 12 :fun "iter" f))
    (plotly/base {:=height 420 :=width 700})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    (plotly/plot)
    (assoc-in [:data 0 :line :dash] "dot") ; https://plotly.com/javascript/reference/scatter/#scatter-line-dash 
    (assoc-in [:data 0 :name] "<em>y=x</em>") ; https://plotly.com/javascript/reference/scatter/#scatter-name
    (assoc-in [:data 1 :line :width] 1) ; default is 2.  https://plotly.com/javascript/reference/scatter/#scatter-line-width
    (assoc-in [:data 1 :name] "<em>f(x)=xe<sup>r(1-x)</sup></em>") ; 1 shouldn't really be italicized
    (assoc-in [:data 2 :line :dash] "dot")
    (sp/equalize-display-units) ; If display dimensions don't fit data, extra space in plot
    ;(kind/pprint)
    ))



;; ## Vega-lite/Hanami examples

(kind/vega-lite (sh/make-vl-spec [0.0 3.0] um/normalized-ricker [2.5] [1] [0.6] 60))
(kind/vega-lite (sh/vl-plot-seq "stuff" (take 100 (iterate (um/normalized-ricker 2.5) 0.6))))
(kind/vega-lite (sh/histogram 100 (take 10000 (iterate (um/normalized-ricker 2.5) 0.6))))
(kind/vega-lite (sh/make-vl-spec [0.0 3.0] 
                                 (msc/n-comp (um/normalized-ricker 2.5) 80)
                                 [] [1] [1.6] 10))
(kind/vega-lite (sh/make-vl-spec [0.0 3.0] 
                                 (msc/n-comp (um/normalized-ricker 2.5) 80)
                                 [] [1] [2.0] 10))
(kind/vega-lite (sh/make-vl-spec [0.0 3.0] 
                                 (msc/n-comp (um/normalized-ricker 2.5) 80)
                                 [] [1] [0.8] 10))
(kind/vega-lite (sh/make-vl-spec [0.0 3.0] 
                                 (msc/n-comp (um/normalized-ricker 2.5) 80)
                                 [] [1] [1.2] 10))
(kind/vega-lite (sh/make-vl-spec [0.0 3.0] 
                                 (msc/n-comp (um/normalized-ricker 2.5) 80)
                                 [] [1] [1.0] 10))
(kind/vega-lite (sh/make-vl-spec [0.0 3.0] 
                                 (msc/n-comp (um/normalized-ricker 2.5) 80)
                                 [] [1] [0.99 1.01] 10))


^:kindly/hide-code
(comment
  (kind/vega-lite (sh/histogram 100 (take 10000 (iterate (um/logistic 4.0) 0.6))))
  (kind/vega-lite (sh/make-vl-spec [0.0 1400] um/logistic-plus [1000 3.00] [1] [0.1] 40))

  (kind/vega-lite (sh/vl-plot-seq "1K" (take 1000 (iterate (um/logistic-plus 100000 2.57) 0.1))))

  (kind/vega-lite (sh/make-vl-spec [0.0 5.0] um/pre-ricker [0.3679] [1] [] 1))

  (kind/vega-lite (sh/make-vl-spec [0.0 5.0] um/original-ricker [] [1] [] 1))


  (kind/vega-lite (sh/make-vl-spec [0.0 1.0] um/logistic [3.0] [1] [0.99] 10))

  (kind/vega-lite (sh/make-vl-spec [0.0 1.0] um/logistic [3.0] (msc/irange 1 6)
                             [0.05 0.075 0.15] 3
                             :fixedpt-x 0.5
                             :addl-plots [(sh/horiz 1.0)]))
)

^:kindly/hide-code
(comment
  (require 'clojure.repl)
  (clojure.repl/pst)
)
