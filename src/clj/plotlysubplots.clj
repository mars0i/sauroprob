^:kindly/hide-code
(ns plotlysubplots
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))

^:kindly/hide-code
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

^:kindly/hide-code
(def cobthing
  (let [comps [1]]
    (-> (fns/plot-fns-with-cobweb {:x-max 3
                                   :fs (map (partial msc/n-comp f) comps)
                                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                                   :init-x init-x
                                   :n-cobweb 16
                                   :n-seq-iterates 400
                                   :n-dist-iterates n-hist-iterates}))))

^:kindly/hide-code
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

^:kindly/hide-code
;; Split the traces into a grid of plots:
;; The values in in xn yn determines the order in the grid.
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

;(kind/pprint multiplotthing)
