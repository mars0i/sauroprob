^:kindly/hide-code
(ns iterfreqs3
  (:require ;[clojure.math :as m]
            [fastmath.random :as fr]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))

;; Make LaTeX work in Plotly labels:
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

(def n-iterates 100000)

;; Better:
(let [f um/logistic-4
      comps [1]]
  (fns/plots-grid {:x-max 1
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.901
                    :n-cobweb 30
                    :n-seq-iterates 400
                    :n-dist-iterates n-iterates}))

;; Let's try a different init x:
(let [f um/logistic-4
      comps [1]]
  (fns/plots-grid {:x-max 1
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.4
                    :n-cobweb 30
                    :n-seq-iterates 400
                    :n-dist-iterates n-iterates}))



;; Again:
(let [f um/logistic-4
      comps [1]]
  (fns/plots-grid {:x-max 1
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.35
                    :n-cobweb 30
                    :n-seq-iterates 400
                    :n-dist-iterates n-iterates}))

;; Again again:
(let [f um/logistic-4
      comps [1]]
  (fns/plots-grid {:x-max 1
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.75
                    :n-cobweb 30
                    :n-seq-iterates 400
                    :n-dist-iterates n-iterates}))

;; This init is the fixed point:
(let [f um/logistic-4
      comps [1]]
  (fns/plots-grid {:x-max 1
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 1
                    :n-cobweb 30
                    :n-seq-iterates 400
                    :n-dist-iterates n-iterates}))

