;; Turchin, _Complex Population Dynamics_, 2003, figure 5.3, p. 149.
;; See p. 148 for partial description of function and paramters.
;; I had to fill in the rest.

^:kindly/hide-code
(ns turchin-complexpopdyn-fig53
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



;; NOT RIGHT based on how the plot is coming out
(defn turchin53
  "Returns a version of the function like the one that Turchin 2003 uses to
  generate figure 5.3.  jog-slope should usually be a small negative
  number.  jog-width should be positive, and usually should be small.  r is
  the parameter of a normalized Ricker map."
  [jog-width jog-slope r x]
  (let [half-width (/ jog-width 2)]
    (if (and (> x (- 1 half-width))
             (< x (+ 1 half-width)))
      (+ (* jog-slope x) (+ 1 jog-slope)) ; alter the intercept so line goes through (1,1)
      (um/normalized-ricker r x))))


^:kindly/hide-code
;(def n-cob 8)

(def n-dist-iters 10000)
(def n-seq-iters 100)
(def x-max 4.0)
(def init-x 0.91)


(let [f (partial turchin53 0.25 -0.2 3.5)
      comps [1]]
  (fns/plots-grid {:x-max x-max
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x init-x
                   :n-cobweb 8
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters
                   :intro-label-md (str "$r=" 4 ":$") 
                  }))
