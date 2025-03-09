^:kindly/hide-code
(ns sauroprob.core
  (:require [clojure.math :as m] ; new in Clojure 1.11 
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.clay.v2.api :as clay] ; needed for clay eval keymappings
            ;[scicloj.metamorph.ml.rdatasets :as rdatasets]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.hanami :as sh]
            [sauroprob.plotly :as sp]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; ## Plotly examples

(def logistic-iter-data
  (let [μ 1.5, f (um/normalized-ricker μ)]
    (tc/concat
      (tc/dataset {:x [0 2], :y [0 2], :fun "y=x"})
      (sp/fn2dataset [0 2] :fun "f" f)
      (sp/fn2dataset [0 2] :fun "f<sup>2</sup>" (msc/n-comp f 2))
      ;(sp/iter-lines 0.05 12 :fun "iteration" f)
      )))


;    (sp/equalize-display-units)

(-> logistic-iter-data
    (plotly/base {:=height 300 :=width 380})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    (sp/equalize-display-units)
    (plotly/plot)
    ;; Set properties of lines:
;    (assoc-in [:data 0 :line :width] 1.5) ; default is 2. 
;    (assoc-in [:data 1 :line :width] 1)
;    (assoc-in [:data 2 :line :width] 1.5)
;    (assoc-in [:data 0 :line :dash] "dash") 
;    (assoc-in [:data 2 :line :dash] "dot")
    ;; Set legend and rollover labels to something other than the value of :fun :
;    (assoc-in [:data 0 :name] "<em>y=x</em>")
;    (assoc-in [:data 1 :name] "<em>f(x)=xe<sup>μ(1-x)</sup></em>") ; 1 shouldn't really be italicized
    ;(kind/pprint)
    )
;; https://plotly.com/javascript/reference/scatter/#scatter-name
;; https://plotly.com/javascript/reference/scatter/#scatter-line-dash 
;; https://plotly.com/javascript/reference/scatter/#scatter-line-width


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

;; How to select a specific curve from dataset
;; (cf. https://scicloj.github.io/tablecloth/#select-1):
(-> logistic-iter-data
    (tc/select-rows (comp #(= "f" %) :fun))
    (plotly/layer-line {:=x :x, :=y, :y})
    (sp/equalize-display-units))

^:kindly/hide-code
(comment
  (require 'clojure.repl)
  (clojure.repl/pst)
)

;; ## MWE

(-> (tc/concat
      (tc/dataset {:x [0 1], :y [0 1], :equation "y = x"})
      (tc/dataset {:x [0 1], :y [1 0], :equation "y = -x"}))
    (plotly/layer-line {:=x :x, :=y, :y :=color :equation})
    (plotly/plot)
    (assoc-in [:data 0 :line :width] 1) ; default is 2. 
    (assoc-in [:data 1 :line :width] 3)
    (assoc-in [:data 0 :line :dash] "dash") 
    (assoc-in [:data 1 :line :dash] "dot"))


;; This version avoids the fragility of using array indexes but is worse in
;; other respects:
(merge-with into
            (-> (tc/dataset {:x [0 1], :y [0 1], :equation "y = x"})
                (plotly/layer-line {:=x :x, :=y, :y :color :equation})
                (plotly/plot)
                (assoc-in [:data 0 :line :width] 1)
                (assoc-in [:data 0 :line :dash] "dash")
                (assoc-in [:data 0 :name] "y = x"))
            (-> (tc/dataset {:x [0 1], :y [1 0], :equation "y = -x"})
                (plotly/layer-line {:=x :x, :=y, :y :color :equation})
                (plotly/plot)
                (assoc-in [:data 0 :line :width] 3)
                (assoc-in [:data 0 :line :dash] "dot")
                (assoc-in [:data 0 :name] "y = -x")))
