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

;; This is below the chaos min:
(let [f (um/normalized-ricker 2.6)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.901
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates 1000000}))

;; Well, this seems pretty periodic, though it's (just above the chaos min):
(let [f (um/normalized-ricker 2.6925)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.901
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates 1000000}))

;; Still kinda periodic, but maybe with intervening noise:
(let [f (um/normalized-ricker 2.7)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.901
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates 1000000}))

;; Better:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.901
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates 1000000}))

;; Let's try a different init x:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.4
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates 1000000}))



;; Again:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.35
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates 1000000}))

;; Again again:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.75
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates 1000000}))

;; This init is the fixed point:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 1
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates 1000000}))

