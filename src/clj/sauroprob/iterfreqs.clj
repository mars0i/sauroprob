^:kindly/hide-code
(ns iterfreqs
  (:require ;[clojure.math :as m]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))

^:kindly/hide-code
;; Make LaTeX work in Plotly labels:
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

um/ricker-chaos-min

(def n-dist-iters 10000)
(def n-seq-iters 200)
(def n-cob 10)

;; This is below the chaos min:
(let [f (um/normalized-ricker 2.6)
      comps [1]]
  (fns/plots-grid {:x-max 4
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x 0.901
                   :n-cobweb n-cob
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters}))

;; Well, this seems pretty periodic, though it's (just above the chaos min):
(let [f (um/normalized-ricker 2.6925)
      comps [1]]
  (fns/plots-grid {:x-max 4
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x 0.901
                   :n-cobweb n-cob
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters}))

;; Still kinda periodic, but maybe with intervening noise:
(let [f (um/normalized-ricker 2.7)
      comps [1]]
  (fns/plots-grid {:x-max 4
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x 0.901
                   :n-cobweb n-cob
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters}))

;; Better:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/plots-grid {:x-max 4
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x 0.901
                   :n-cobweb n-cob
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters}))

;; Let's try a different init x:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/plots-grid {:x-max 4
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x 0.4
                   :n-cobweb n-cob
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters}))



;; Again:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/plots-grid {:x-max 4
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x 0.35
                   :n-cobweb n-cob
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters}))

;; Again again:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/plots-grid {:x-max 4
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x 0.75
                   :n-cobweb n-cob
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters}))

;; This init is the fixed point:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/plots-grid {:x-max 4
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x 1
                   :n-cobweb n-cob
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters}))

