(ns mwe3
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [tablecloth.api :as tc]))

(def data1 (tc/concat
            (tc/dataset {:x [0 1], :y [0 1], :fun "y=x" :my-size 10})
            (tc/dataset {:x [0.5 0.5 0.5], :y [0 0.5 1], :fun "y=x" :my-size 400})
            (tc/dataset {:x [0 1], :y [1 0], :fun "y=-x" :my-size 800})))

(def data2 (tc/concat
             (tc/dataset {:x [0 1], :y [0 1], :fun "y=x" :my-size 50})
             (tc/dataset {:x [0 1], :y [1 0], :fun "y=-x" :my-size 50})))

;; This plot displays as expected:
(-> data1
    (plotly/layer-point {:=x :x, :=y :y, :=size :my-size :=size-range [10 100]})
    (plotly/plot))

(-> data1
    (plotly/layer-point {:=x :x, :=y :y, :=size :my-size})
    (plotly/plot)
    (kind/pprint))

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
