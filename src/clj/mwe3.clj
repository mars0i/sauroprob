(ns mwe3
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [tablecloth.api :as tc]))


(def data3 (tc/concat
            (tc/dataset {:x [0], :y [0], :fun "line1" :my-size 20})
            (tc/dataset {:x [1], :y [1], :fun "line1" :my-size 60})
            (tc/dataset {:x [0], :y [1], :fun "line2" :my-size 200})
            (tc/dataset {:x [1], :y [0], :fun "line2" :my-size 600})))

(-> data3
    (plotly/layer-point {:=x :x, :=y :y, :=size :my-size, :=color :fun}))

;; This shows that that using `:=size` doesn't affect different lines.
;; But that's a consequence of the fact that `:=size-range` is applied to
;; each group separately.
(-> data3
    (plotly/layer-line {:=x :x, :=y :y, :=size :my-size, :=color :fun}))

;; Note that :size is not getting substituted by :width in :line:
(-> data3
    (plotly/layer-line {:=x :x, :=y :y, :=size :my-size, :=color :fun})
    (plotly/plot)
    (kind/pprint))

(def data3plot 
  (-> data3
      (plotly/layer-line {:=x :x, :=y :y, :=size :my-size, :=color :fun})
      (plotly/plot)))

(assoc-in data3plot [:data 0 :line :width] 5)
;; Giving :width a sequence arg (rather than a number) causes :width to have no effect:
(assoc-in data3plot [:data 0 :line :width] (get-in data3plot [:data 0 :line :size]))


;; Note the same :fun value has two different sizes:
(def data1 (tc/concat
            (tc/dataset {:x [0 1], :y [0 1], :fun "y=x" :my-size 10})
            (tc/dataset {:x [0.5 0.5 0.5], :y [0 0.5 1], :fun "y=x" :my-size 400})
            (tc/dataset {:x [0 1], :y [1 0], :fun "y=-x" :my-size 800})))


;; This plot displays as expected:
(-> data1
    (plotly/layer-point {:=x :x, :=y :y, :=size :my-size})
    (plotly/plot))

(-> data1
    (plotly/layer-point {:=x :x, :=y :y, :=size :my-size})
    (plotly/plot)
    (kind/pprint))


;; div by zero if you plot it with same sizes
(def data2 (tc/concat
             (tc/dataset {:x [0 1], :y [0 1], :fun "y=x" :my-size 50})
             (tc/dataset {:x [0 1], :y [1 0], :fun "y=-x" :my-size 50})))

;; Adding plotly/plot or plotting this (which does that) causes div by zero
(-> data2
    (plotly/layer-point {:=x :x, :=y :y, :=size :my-size})
    (kind/pprint))


(comment
  ;; These plots, in which the size is uniform for all points within a group,
  ;; generate a divide by zero error when they are displayed as plots using
  ;; Clay, or when they are passed to `plotly/plot`.

  (-> data1
      (plotly/layer-point {:=x :x, :=y :y, :=color :fun, :=size :my-size}))

  (-> data2
      (plotly/layer-point {:=x :x, :=y :y, :=size :my-size}))
)
