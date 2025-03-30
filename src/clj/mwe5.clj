(ns mwe5
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]))

(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

;; ## Allow Plotly to decide on visual distances for x and y:

;; ### Example 1--simple default behavior:
(-> (tc/concat (tc/dataset {:x [0 1], :y [0 1], :fun "r"})
               (tc/dataset {:x [0 1], :y [1 0], :fun "s"}))
    (plotly/layer-line {:=x :x, :=y :y, :=color :fun})
    plotly/plot)

;; ### Example 2--long label causes the legend to squeeze the plot.
;; I think this is Plotly's intended default behavior.
(-> (tc/concat (tc/dataset {:x [0 1], :y [0 1], :fun "r"})
               (tc/dataset {:x [0 1], :y [1 0], :fun "s-------------------------------------------"}))
    (plotly/layer-line {:=x :x, :=y :y, :=color :fun})
    plotly/plot)

;; ### Example 3--when MathJax runs, a long label causes the legend
;; to overlay the whole plot while squeezing the plotted lines:
(-> (tc/concat (tc/dataset {:x [0 1], :y [0 1], :fun "$r$"})
               (tc/dataset {:x [0 1], :y [1 0], :fun "s-------------------------------------------"}))
    (plotly/layer-line {:=x :x, :=y :y, :=color :fun})
    plotly/plot)

;; ## Require visual x and y distances to be the same:

;; Here we use `:scaleanchor` and `:scaleratio` to force visual x and y
;; units to be of equal size.

;; ### Example 4--no LaTeX, so the long label causes legend to squeeze the whole plot, 
;; but data is represented in equal-sized visual units:
(-> (tc/concat (tc/dataset {:x [0 1], :y [0 1], :fun "r"})
               (tc/dataset {:x [0 1], :y [1 0], :fun "s-------------------------------------------"}))
    (plotly/layer-line {:=x :x, :=y :y, :=color :fun})
    plotly/plot
    (assoc-in [:layout :yaxis :scaleanchor] :x)
    (assoc-in [:layout :yaxis :scaleratio] 1))

;; ### Example 5--like the previous plot, but with LaTex.
;; It looks like #3 at first, but if you click on the Autoscale button, it
;; takes the form of #4.
(-> (tc/concat (tc/dataset {:x [0 1], :y [0 1], :fun "$r$"})
               (tc/dataset {:x [0 1], :y [1 0], :fun "s-------------------------------------------"}))
    (plotly/layer-line {:=x :x, :=y :y, :=color :fun})
    plotly/plot
    (assoc-in [:layout :yaxis :scaleanchor] :x)
    (assoc-in [:layout :yaxis :scaleratio] 1))
