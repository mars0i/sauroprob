(ns mwe3
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]))

(def data1 (tc/concat
            (tc/dataset {:x [0 1], :y [0 1], :fun "y=x" :my-size 10})
            (tc/dataset {:x [0 1], :y [1 0], :fun "y=-x" :my-size 50})))

(def data2 (tc/concat
             (tc/dataset {:x [0 1], :y [0 1], :fun "y=x" :my-size 50})
             (tc/dataset {:x [0 1], :y [1 0], :fun "y=-x" :my-size 50})))

(-> data1
    (plotly/layer-point {:=x :x, :=y :y, :=size :my-size}))

(comment
  (-> data1
      (plotly/layer-point {:=x :x, :=y :y, :=color :fun, :=size :my-size}))

  (-> data2
      (plotly/layer-point {:=x :x, :=y :y, :=size :my-size}))
)
