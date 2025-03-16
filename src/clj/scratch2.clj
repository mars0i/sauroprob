^:kindly/hide-code
(ns scratch
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
    (assoc-in [:data 0 :line :width] 1.5) ; default is 2. 
    (assoc-in [:data 1 :line :width] 1)
    (assoc-in [:data 2 :line :width] 1.5)
    (assoc-in [:data 0 :line :dash] "dash") 
    (assoc-in [:data 2 :line :dash] "dot")
    ;; Set legend and rollover labels to something other than the value of :fun :
    (assoc-in [:data 0 :name] "<em>y=x</em>")
    (assoc-in [:data 1 :name] "<em>f(x)=xe<sup>μ(1-x)</sup></em>") ; 1 shouldn't really be italicized
    (kind/pprint)
)
;; https://plotly.com/javascript/reference/scatter/#scatter-name
;; https://plotly.com/javascript/reference/scatter/#scatter-line-dash 
;; https://plotly.com/javascript/reference/scatter/#scatter-line-width
