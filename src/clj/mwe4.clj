;; Posted at https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/Plotly.20plots.20are.20assumed.20to.20have.20fixed.20height.20in.20Clay

;; Not sure if this is an issue with Clay, Kindly, or
;; Tableplot/Plotly.  If I change the height of a Plotly plot, the
;; location of elements displayed by Clay doesn't shift to reflect
;; the height of the plot.  In the example below, the bottom of the
;; second plot is cut off by the next element, and  the third plot
;; has extra space below it.  I see the same effect with
;; `layer-line` and `layer-histogram`, so I assume it happens with
;; all Plotly plots in Clay.

(ns mwe4
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [tablecloth.api :as tc]))

;; With default height, no overlap:
(-> (tc/dataset {:x [0 1], :y [0 1]})
    (plotly/layer-point {:=x :x, :=y :y :=mark-size 60}))

;; With extra height, the bottom is overlayed by the next element in the file:
(-> (tc/dataset {:x [0 1], :y [0 1]})
    (plotly/base {:=height 600})
    (plotly/layer-point {:=x :x, :=y :y :=mark-size 60}))

;; With lessened height, there's extra space:
(-> (tc/dataset {:x [0 1], :y [0 1]})
    (plotly/base {:=height 200})
    (plotly/layer-point {:=x :x, :=y :y :=mark-size 60}))

(kind/md "**This is the next element in the file.**")
