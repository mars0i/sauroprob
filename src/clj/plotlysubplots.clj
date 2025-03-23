^:kindly/hide-code
(ns plotlysubplots
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))

;; See plotlysubplot-variations for other ways to do this.

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
        :data (vec (mapcat :data ; the order here determines trace indexes:
                           [(fns/plot-iterates (take 250 iterates))
                            (fns/plot-cdf 3 (take n-hist-iterates iterates))
                            (fns/plot-iterates-histogram (take n-hist-iterates iterates))
                            cobthing]))}))
                            ;; Put cobthing last because it uses multiple
                            ;; traces, and I may add and remove traces (i.e. f^2, f^3, etc.)
                            ;; By putting it last, indexes of the other traces needn't change.


^:kindly/hide-code
(-> combothing
    ;; Overall dimensions of multiplot:
    (assoc-in [:layout :width] 1000)
    (assoc-in [:layout :height] 600)
    ;; Split into 2x2 subplots
    (assoc-in [:layout :grid] {:rows 2, :columns 2, :pattern "independent"})
    ;; Map traces (first arg) to order of plots (second arg) left to right, top to bottom:
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

