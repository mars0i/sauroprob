^:kindly/hide-code
(ns iterfreqs
  (:require [clojure.math :as m]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            ;[scicloj.clay.v2.api :as clay] ; needed for clay eval keymappings
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            ;[sauroprob.hanami :as sh]
            [sauroprob.plotly :as sp]))

(let [_ nil]
 (kind/md "Before")
 (kind/md "---")
 (kind/md "After"))

(let [plot1 (plotly/layer-line 
              (tc/dataset {:x [0 2], :y [0 2], :fun "y=x"})
              {:=x :x, :=y, :y})
      plot2 (plotly/layer-line 
              (tc/dataset {:x [0 2], :y [2 0], :fun "y=-x"})
              {:=x :x, :=y, :y})]
  plot1
  plot2
  )


;(let [param1 ...
;      param2 ...]
;  (-> (tc/dataset <code that depends on the parameters>)
;      (plotly/layer-line {:x ...}))
;  (-> (tc/dataset <Other code that depends on the parameters>)
;      (plotly/layer-histogram {:x ...})))


(let [max-val 2]
  (-> (tc/dataset {:x [0 max-val], :y [0 max-val], :fun "y=x"})
      (plotly/layer-line {:=x :x, :=y, :y}))
  (-> (tc/dataset {:x [0 max-val], :y [max-val 0], :fun "y=-x"})
      (plotly/layer-line {:=x :x, :=y, :y})))

(def mv 2)

(plotly/layer-line 
  (tc/dataset {:x [0 mv], :y [0 mv], :fun "y=x"})
  {:=x :x, :=y, :y})

(plotly/layer-line 
  (tc/dataset {:x [0 mv], :y [mv 0], :fun "y=-x"})
  {:=x :x, :=y, :y})

(defmacro two-plots
  [max-val]
  `(do
     (plotly/layer-line 
       (tc/dataset {:x [0 ~max-val], :y [0 ~max-val], :fun "y=x"})
       {:=x :x, :=y, :y})
     (plotly/layer-line 
       (tc/dataset {:x [0 ~max-val], :y [~max-val 0], :fun "y=-x"})
       {:=x :x, :=y, :y})))

(two-plots 3)

(comment
(let [catkey :fun
      μ 1.5
      f (um/normalized-ricker μ)
      init-x 0.25
      iterates (iterate f init-x)
      n-iterates 100]
  ;; Plot of function itself, with a few iterations shown:
  (-> (tc/concat (sp/iter-lines init-x 8 catkey "iter" f)
                 (tc/dataset {:x [0 2], :y [0 2], catkey "y=x"})
                 (sp/fn2dataset [0 2] catkey "f" f)
                 (sp/fn2dataset [0 2] catkey "f<sup>2</sup>" (msc/n-comp f 2)))
      (plotly/base {:=height 400 :=width 450})
      (plotly/layer-line {:=x :x, :=y, :y :=color catkey})
      (sp/equalize-display-units)
      (assoc-in [:data 0 :line :width] 1.5)
      (assoc-in [:data 0 :line :dash] "dot"))
  ;; Plot of a sequence of values of the function:
  (-> (tc/dataset {:x (range n-iterates)
                   :y (take n-iterates iterates)})
      (plotly/layer-line {:=x :x, :=y, :y}))

  )
)
