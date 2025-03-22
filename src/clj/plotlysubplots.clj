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


^:kindly/hide-code
(comment
(def data3 (tc/concat
             ;; These are going to be four distinct "traces" in the plotly config:
             (tc/dataset {:x [0], :y [0], :fun "line1" :my-size 20})
             (tc/dataset {:x [1], :y [1], :fun "line1" :my-size 60})
             (tc/dataset {:x [0], :y [1], :fun "line2" :my-size 200})
             (tc/dataset {:x [1], :y [0], :fun "line2" :my-size 600})))

(def uniplot (-> data3
                 (plotly/layer-point {:=x :x, :=y :y, :=size :my-size, :=color :fun})
                 (plotly/layer-line {:=x :x, :=y :y, :=size :my-size, :=color :fun})
                 (plotly/plot)))

uniplot

(def multiplot (-> uniplot
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
                   ))


multiplot

(kind/pprint uniplot)

(kind/pprint multiplot)

;; ---

(def uniricker
  (let [f (um/normalized-ricker 3.0)
        comps [1 4]]
    (-> (fns/plot-fns-with-cobweb {:x-max 4
                                   :fs (map (partial msc/n-comp f) comps)
                                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                                   :init-x 2.1
                                   :n-cobweb 10
                                   :n-seq-iterates 400
                                   :n-dist-iterates n-hist-iterates}))))

(def multiricker
  (-> uniricker
      (assoc-in [:data 0 :xaxis] "x1") ; default--could be left out
      (assoc-in [:data 0 :yaxis] "y1")
      (assoc-in [:data 1 :xaxis] "x2")
      (assoc-in [:data 1 :yaxis] "y2")
      (assoc-in [:data 2 :xaxis] "x1")
      (assoc-in [:data 2 :yaxis] "y1")
      (assoc-in [:data 3 :xaxis] "x6")
      (assoc-in [:data 3 :yaxis] "y6")
      (assoc-in [:layout :grid] {:rows 3, :columns 2, :pattern "independent"})))

multiricker

(kind/pprint multiricker)
)

;; ---


(def f (um/normalized-ricker 3.0))
(def init-x 2.1)
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

(def combothing
  (plotly/plot ; add meta
       {:layout (:layout cobthing)
        ;; merge the traces:
        :data (vec (mapcat :data 
                           [cobthing
                            (fns/plot-iterates (take 250 iterates))
                            (fns/plot-iterates-histogram (take n-hist-iterates iterates))
                            (fns/plot-cdf 3 (take n-hist-iterates iterates))]))}))
;(clojure.repl/pst)

;(class (:data combothing))


;(kind/pprint combothing)

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
      (assoc-in [:layout :xaxis2] {:anchor "y2", :domain [0.3, 1.0]})
      (assoc-in [:layout :xaxis4] {:anchor "y4", :domain [0.3, 1.0]})

      ;(assoc-in [:data 0 :line :width] 0.5) ; s/b the diagonal y=x line
  ))

multiplotthing

;; ---
