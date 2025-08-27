;; Turchin, _Complex Population Dynamics_, 2003, cf. figure 5.3, p. 149.
;; See p. 148 for partial description of function and parameters.

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
;; Make LaTeX work in Plotly labels.
;; Side effects: Shifts labels in plotly, makes Clay not return to same position.
;(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

^:kindly/hide-code
(declare turchin53)
;; turchin53 is defined below

;; Different init-x's allow the chaos end sooner, or later.  It just
;; depends how soon a value gets trapped in the basin of attraction
;; where the abs val of the slope is < 1..
(def common-params {:x-max 4.0
                    :init-x 1.8075 ; 1.8075 causes chaos to persist well past 200
                    :n-cobweb 30
                    :n-seq-iterates 280
                    ;:intro-label-md (str "$r=" 3.5 ":$") 
                   })


;; This is like a Ricker function with r=3.5, but near 1, it's
;; linear with a slope in (-1, 0).
(let [f (partial turchin53 0.1 -0.5 3.5)
      comps [1]]
  (fns/plots-grid (merge 
                    {:fs (map (partial msc/n-comp f) comps)
                     :labels (map (fn [n] (str "$f^" n "$")) comps)
                     :n-dist-iterates 280}
                    common-params)))

;; This is the original Ricker function without the modification near 1.
(let [f (partial um/normalized-ricker 3.5)
      comps [1]]
  (fns/plots-grid (merge 
                    {:fs (map (partial msc/n-comp f) comps)
                     :labels (map (fn [n] (str "$f^" n "$")) comps)
                     :n-dist-iterates 5000}
                    common-params)))

(defn turchin53
  "Returns a version of the function like the one that Turchin 2003 uses to
  generate figure 5.3.  jog-slope should usually be a small negative
  number.  jog-width should be positive, and usually should be small.  r is
  the parameter of a normalized Ricker map."
  [jog-width jog-slope r x]
  (let [half-width (/ jog-width 2)]
    (if (and (> x (- 1 half-width))
             (< x (+ 1 half-width)))
      (+ (* jog-slope x) (+ 1 (- jog-slope))) ; alter the intercept so line goes through (1,1)
      (um/normalized-ricker r x))))
