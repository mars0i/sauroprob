^:kindly/hide-code
(ns iterfreqs2
  (:require ;[clojure.math :as m]
            [fastmath.random :as fr]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
          [scicloj.metamorph.ml.rdatasets :as rdatasets]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))

^:kindly/hide-code
;; Make LaTeX work in Plotly labels:
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

(def iters (take 10000 (iterate (um/normalized-ricker 3) 2.1)))

(def histplot (fns/iterates-histogram iters))
(def cdfplot (-> (fns/plot-cdf 3.5 iters)
                 (assoc-in [:layout :title] "CDF, Baby!")
                 ))


(kind/fragment [cdfplot 
                histplot
                (kind/pprint (plotly/plot cdfplot))
                ])


;; ---

(def hist-iterates 100000)

;; Better:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 2.1
                    :n-cobweb 10
                    :n-seq-iterates 400
                    :n-dist-iterates hist-iterates}))

;; Let's try a different init x:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.001
                    :n-cobweb 10
                    :n-seq-iterates 400
                    :n-dist-iterates hist-iterates}))


