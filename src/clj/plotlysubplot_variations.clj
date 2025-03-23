(ns plotlysubplot-variations
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))

;; Make LaTeX work in Plotly labels:
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

(def n-hist-iterates 100000)

;; Exploration of how to make general Plotly subplots (like lattice graphics)

;; For more info, including ways to provide additional customization see:

;; https://plotly.com/javascript/subplots/

;; https://plotly.com/javascript/reference/layout/#layout-grid


(def f (um/normalized-ricker 2.9))
(def init-x 0.3)
(def iterates (iterate f init-x))

(def cobthing
  (let [comps [1]]
    (-> (fns/plot-fns-with-cobweb {:x-max 3
                                   :fs (map (partial msc/n-comp f) comps)
                                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                                   :init-x init-x
                                   :n-cobweb 16
                                   :n-seq-iterates 400
                                   :n-dist-iterates n-hist-iterates}))))

;; A plot with three traces in cobthing, and three other traces
(def combothing
  (plotly/plot ; add meta
               {:layout (:layout cobthing)
                ;; merge the traces:
                :data (vec (mapcat :data 
                                   [cobthing
                                    (fns/plot-iterates (take 250 iterates))
                                    (fns/plot-iterates-histogram (take n-hist-iterates iterates))
                                    (fns/plot-cdf 3 (take n-hist-iterates iterates))]))}))

;; Same thing as above, with but with traces in a different order, i.e.
;; with the cobweb diagram at the end, so that adding new curves to the 
;; cobweb diagram doesn't require changing the other indexes.
(def combothing2
  (plotly/plot ; add meta
               {:layout (:layout cobthing)
                ;; merge the traces:
                :data (vec (mapcat :data ; the order here determines trace indexes:
                                   [(fns/plot-iterates (take 250 iterates))
                                    (fns/plot-cdf 3 (take n-hist-iterates iterates))
                                    (fns/plot-iterates-histogram (take n-hist-iterates iterates))
                                    cobthing]))}))
;; Put cobthing last because it uses multiple
;; traces, and I may add and remove traces (i.e. f^2, f^3, etc.)
;; By putting it last, indexes of the other traces needn't change.

;; Split the traces into a grid of plots:
;; The values in in xn yn determines the order in the grid.
;; (Look at what a PITA this is!  The Tableplot/plotly docs promise facets in the future.)
(def multiplotthing
  (-> combothing
      ;; the cobweb plot
      (assoc-in [:data 0 :xaxis] "x1") ; default--could be left out
      (assoc-in [:data 0 :yaxis] "y1")
      (assoc-in [:data 1 :xaxis] "x1")
      (assoc-in [:data 1 :yaxis] "y1")
      (assoc-in [:data 2 :xaxis] "x1")
      (assoc-in [:data 2 :yaxis] "y1")

      ;; iterating the function over n steps
      (assoc-in [:data 3 :xaxis] "x2")
      (assoc-in [:data 3 :yaxis] "y2")

      ;; the cumulative dist function
      (assoc-in [:data 5 :xaxis] "x3")
      (assoc-in [:data 5 :yaxis] "y3")

      ;; histogram
      (assoc-in [:data 4 :xaxis] "x4")
      (assoc-in [:data 4 :yaxis] "y4")

      ;; Split the preceding into four subplots
      (assoc-in [:layout :grid] {:rows 2, :columns 2, :pattern "independent"})

      ;; Adjust widths of subplots

      ;; Overall width and height of the grid of plots:
      (assoc-in [:layout :width] 1000)
      (assoc-in [:layout :height] 600)
      ;; Make left column narrower:
      (assoc-in [:layout :xaxis] {:anchor "y1", :domain [0, 0.25]})
      (assoc-in [:layout :xaxis3] {:anchor "y3", :domain [0, 0.25]})
      ;; Start right plots to the right of the left plots:
      (assoc-in [:layout :xaxis2] {:anchor "y2", :domain [0.3, 1.0]}) ; why are axis bars shifted left?
      (assoc-in [:layout :xaxis4] {:anchor "y4", :domain [0.3, 1.0]})

      ;(assoc-in [:layout :annotations]
      ;          [{:x 1
      ;            :y 0
      ;            :text "Values from iterating the function"
      ;            :showarrow false}])

      ;(assoc-in [:data 0 :line :width] 0.5) ; s/b the diagonal y=x line
      ))

multiplotthing

;; ---

;; Try it using new convenience function:
(-> combothing
    ;; the cobweb plot
    (sp/assoc-into-trace 0 [:xaxis] "x1") ; default--could be left out
    (sp/assoc-into-trace 0 [:yaxis] "y1")
    (sp/assoc-into-trace 1 [:xaxis] "x1")
    (sp/assoc-into-trace 1 [:yaxis] "y1")
    (sp/assoc-into-trace 2 [:xaxis] "x1")
    (sp/assoc-into-trace 2 [:yaxis] "y1")

    ;; iterating the function over n steps
    (sp/assoc-into-trace 3 [:xaxis] "x2")
    (sp/assoc-into-trace 3 [:yaxis] "y2")

    ;; the cumulative dist function
    (sp/assoc-into-trace 5 [:xaxis] "x3")
    (sp/assoc-into-trace 5 [:yaxis] "y3")

    ;; histogram
    (sp/assoc-into-trace 4 [:xaxis] "x4")
    (sp/assoc-into-trace 4 [:yaxis] "y4")

    ;; Split the preceding into four subplots
    (assoc-in [:layout :grid] {:rows 2, :columns 2, :pattern "independent"})

    ;; Adjust widths of subplots

    ;; Overall width and height of the grid of plots:
    (assoc-in [:layout :width] 1000)
    (assoc-in [:layout :height] 600)
    ;; Make left column narrower:
    (assoc-in [:layout :xaxis] {:anchor "y1", :domain [0, 0.25]})
    (assoc-in [:layout :xaxis3] {:anchor "y3", :domain [0, 0.25]})
    ;; Start right plots to the right of the left plots:
    (assoc-in [:layout :xaxis2] {:anchor "y2", :domain [0.3, 1.0]}) ; why are axis bars shifted left?
    (assoc-in [:layout :xaxis4] {:anchor "y4", :domain [0.3, 1.0]})
    )


;; ---

(-> combothing
    ;; the cobweb plot
    ;; Can I replace the six assoc-in/into-trace calls with two lines?
    ;; Note you need an extra pair of outer parens--the no-paren thread-arrow trick doesn't work.
    ((fn [plot] (reduce (fn [p idx] (sp/assoc-into-trace p idx [:xaxis] "x1")) plot [0 1 2])))
    ((fn [plot] (reduce (fn [p idx] (sp/assoc-into-trace p idx [:yaxis] "y1")) plot [0 1 2])))

    ;; iterating the function over n steps
    (sp/assoc-into-trace 3 [:xaxis] "x2")
    (sp/assoc-into-trace 3 [:yaxis] "y2")

    ;; the cumulative dist function
    (sp/assoc-into-trace 5 [:xaxis] "x3")
    (sp/assoc-into-trace 5 [:yaxis] "y3")

    ;; histogram
    (sp/assoc-into-trace 4 [:xaxis] "x4")
    (sp/assoc-into-trace 4 [:yaxis] "y4")

    ;; Split the preceding into four subplots
    (assoc-in [:layout :grid] {:rows 2, :columns 2, :pattern "independent"})

    ;; Adjust widths of subplots

    ;; Overall width and height of the grid of plots:
    (assoc-in [:layout :width] 1000)
    (assoc-in [:layout :height] 600)
    ;; Make left column narrower:
    (assoc-in [:layout :xaxis] {:anchor "y1", :domain [0, 0.25]})
    (assoc-in [:layout :xaxis3] {:anchor "y3", :domain [0, 0.25]})
    ;; Start right plots to the right of the left plots:
    (assoc-in [:layout :xaxis2] {:anchor "y2", :domain [0.3, 1.0]}) ; why are axis bars shifted left?
    (assoc-in [:layout :xaxis4] {:anchor "y4", :domain [0.3, 1.0]})
    )

;; Try again with new convenience function abstracted from fn above.
(-> combothing
    ;; the cobweb plot
    (sp/assoc-into-traces [0 1 2] [:xaxis] "x1")
    (sp/assoc-into-traces [0 1 2] [:yaxis] "y1")

    ;; iterating the function over n steps
    (sp/assoc-into-trace 3 [:xaxis] "x2")
    (sp/assoc-into-trace 3 [:yaxis] "y2")

    ;; the cumulative dist function
    (sp/assoc-into-trace 5 [:xaxis] "x3")
    (sp/assoc-into-trace 5 [:yaxis] "y3")

    ;; histogram
    (sp/assoc-into-trace 4 [:xaxis] "x4")
    (sp/assoc-into-trace 4 [:yaxis] "y4")

    ;; Split the preceding into four subplots
    (assoc-in [:layout :grid] {:rows 2, :columns 2, :pattern "independent"})

    ;; Adjust widths of subplots

    ;; Overall width and height of the grid of plots:
    (assoc-in [:layout :width] 1000)
    (assoc-in [:layout :height] 600)
    ;; DON't SEEM TO NEED THE ANCHORS:
    ;; Make left column narrower:
    (assoc-in [:layout :xaxis]  {:domain [0, 0.25]})
    (assoc-in [:layout :xaxis3] {:domain [0, 0.25]})
    ;; Start right plots to the right of the left plots:
    (assoc-in [:layout :xaxis2] {:domain [0.3, 1.0]}) ; why are axis bars shifted left?
    (assoc-in [:layout :xaxis4] {:domain [0.3, 1.0]})
    )


;; Split the traces into a grid of plots:
;; The assocs can go in any order, but the values xn yn determine the order
;; in the grid, i.e. left to write across a row and then down, x1 is first,
;; then x2, x3, and so on.
(-> combothing2
    ;; the cobweb plot
    (sp/assoc-into-traces [3 4 5] [:xaxis] "x1")
    (sp/assoc-into-traces [3 4 5] [:yaxis] "y1")

    ;; iterating the function over n steps
    (sp/assoc-into-trace 0 [:xaxis] "x2")
    (sp/assoc-into-trace 0 [:yaxis] "y2")

    ;; the cumulative dist function
    (sp/assoc-into-trace 1 [:xaxis] "x3")
    (sp/assoc-into-trace 1 [:yaxis] "y3")

    ;; histogram
    (sp/assoc-into-trace 2 [:xaxis] "x4")
    (sp/assoc-into-trace 2 [:yaxis] "y4")

    ;; Split the preceding into four subplots
    (assoc-in [:layout :grid] {:rows 2, :columns 2, :pattern "independent"})

    ;; Adjust widths of subplots

    ;; Overall width and height of the grid of plots:
    (assoc-in [:layout :width] 1000)
    (assoc-in [:layout :height] 600)
    ;; DON't SEEM TO NEED THE ANCHORS:
    ;; Make left column narrower:
    (assoc-in [:layout :xaxis]  {:domain [0, 0.25]})
    (assoc-in [:layout :xaxis3] {:domain [0, 0.25]})
    ;; Start right plots to the right of the left plots:
    (assoc-in [:layout :xaxis2] {:domain [0.3, 1.0]}) ; why are axis bars shifted left?
    (assoc-in [:layout :xaxis4] {:domain [0.3, 1.0]})
    ;kind/pprint
    )


(-> combothing2
    (assoc-in [:layout :width] 1000)
    (assoc-in [:layout :height] 600)
    ;; Split into four subplots
    (assoc-in [:layout :grid] {:rows 2, :columns 2, :pattern "independent"})
    (sp/set-subplot-order [3 4 5] 1) ; the cobweb plot
    (sp/set-subplot-order [0] 2)     ; iterating the function over n steps
    (sp/set-subplot-order [1] 3)     ; the cumulative dist function
    (sp/set-subplot-order [2] 4)     ; histogram
    ;; Adjust widths of subplots, with left column narrower:
    (assoc-in [:layout :xaxis]  {:domain [0, 0.25]})
    (assoc-in [:layout :xaxis3] {:domain [0, 0.25]})
    (assoc-in [:layout :xaxis2] {:domain [0.3, 1.0]}) ; why are axis bars shifted left?
    (assoc-in [:layout :xaxis4] {:domain [0.3, 1.0]})
    ;kind/pprint
    )
