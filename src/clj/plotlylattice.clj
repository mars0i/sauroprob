(ns plotlysubplots
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [tablecloth.api :as tc]))

;; Exploration of how to make general Plotly subplots (like lattice graphics)

;; For more info, including ways to provide additional customization see:

;; https://plotly.com/javascript/subplots/

;; https://plotly.com/javascript/reference/layout/#layout-grid


(def data3 (tc/concat
             ;; These are going to be four distinct "traces" in the plotly config:
             (tc/dataset {:x [0], :y [0], :fun "line1" :my-size 20})
             (tc/dataset {:x [1], :y [1], :fun "line1" :my-size 60})
             (tc/dataset {:x [0], :y [1], :fun "line2" :my-size 200})
             (tc/dataset {:x [1], :y [0], :fun "line2" :my-size 600})))

(-> data3
    (plotly/layer-point {:=x :x, :=y :y, :=size :my-size, :=color :fun})
    (plotly/layer-line {:=x :x, :=y :y, :=size :my-size, :=color :fun})
    (plotly/plot)
    ;; Do nothing to traces 0 and 1.  They will be part of the first subplot.
    ;; But if you name something "x1" and "y1", it will go there too.
    ;; By adding the :xaxis and :yaxis keywords, and giving them one of the
    ;; official names "x2" and "y2", they go on a second subplot.
    (assoc-in [:data 2 :xaxis] "x2")
    (assoc-in [:data 2 :yaxis] "y2")
    ;; And here's the third subplot:
    (assoc-in [:data 3 :xaxis] "x3")
    (assoc-in [:data 3 :yaxis] "y3")
    ;; https://plotly.com/javascript/reference/layout/#layout-grid-pattern
    ;; `:pattern "independent"` seems to be necessary to allow plotly to
    ;; work out the placement on its own.  The alternative, "coupled",
    ;; seems to require more configuration info.  That's a guess.
    (assoc-in [:layout :grid] {:rows 2, :columns 2, :pattern "independent"})
    ;; Since there are only three subplots, the fourth cell will be empty.
    ;(kind/pprint)
    )
