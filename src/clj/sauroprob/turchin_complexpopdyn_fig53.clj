;; Code inspired by Turchin, _Complex Population Dynamics_, 2003,
;; figure 5.3, p. 149. See p. 148 for partial description of function, parameters.

^:kindly/hide-code
(ns turchin-complexpopdyn-fig53
  (:require ;[clojure.math :as m]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly] ; :refer [hide-code] not working with ^:
            [tablecloth.api :as tc]
            [fastmath.random :as fr]
            ;[fastmath.stats :as fs]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))

^:kindly/hide-code
;; Make LaTeX work in Plotly labels.
;; Side effects: Shifts labels in plotly, makes Clay not return to same position.
;(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

^:kindly/hide-code
;; I tried to move this later and forward declare it, but that doesn't seem to work.
(defn turchin53
  "Returns a version of the function like the one that Turchin 2003 uses to
  generate figure 5.3.  jog-slope should usually be a small negative
  number.  This will be the slope of the 'jog', the region in which the
  Ricker function is replaced with a linear function.  jog-min and jog-max
  should be numbers near 1 s.t. 1 falls within (jog-min, jog-max). This is
  the interval in which the Ricker function will be replaced by the linear
  'jog' function. r is the parameter of a normalized Ricker map."
  [jog-min jog-max jog-slope r x]
  (if (and (> x jog-min)
           (< x jog-max))
    (+ (* jog-slope x) (+ 1 (- jog-slope))) ; alter the intercept so line goes through (1,1)
    (um/normalized-ricker r x)))

(def jog-width 0.1)
(def half-width (/ jog-width 2))
(def jog-min (- 1 half-width))
(def jog-max (+ 1 half-width))

(def rng (fr/rng :well1024a 778914531))

(defn noisy-fn
  [rng jog-min jog-max f x]
  (+ (f x) 
     (fr/drandom rng jog-min jog-max)))


;; Different init-x's allow the chaos end sooner, or later.  It just
;; depends how soon a value gets trapped in the basin of attraction
;; where the abs val of the slope is < 1..
(def common-params {:x-max 4.0
                    ;:init-x 1.01 ; works well for jog-width 0.01
                    :init-x 1.8075 ; works well for jog-width 0.1
                    :n-cobweb 30
                    :n-seq-iterates 280
                    ;:intro-label-md (str "$r=" 3.5 ":$") 
                   })


;; This is a Ricker function with a "jog" near (1,1).  It's like a Ricker 
;; function with r=3.5, but near 1, it's linear with a small negative slope.
(let [f (partial turchin53 jog-min jog-max -0.5 3.5)
      comps [1]]
  (fns/plots-grid (merge 
                    {:fs (map (partial msc/n-comp f) comps)
                     :labels (map (fn [n] (str "$f^" n "$")) comps)
                     :n-dist-iterates 280}
                    common-params)))

;; This is a Ricker function with a "jog" near (1,1) and added noise.
;; Without noise, it would be like a Ricker function with r=3.5, but near 1, 
;; linear with a small negative slope.
(let [f (partial noisy-fn rng 0.89 1.11 ; (- 1 (* 1.5 half-width)) (+ 1 (* 1.5 half-width))
                 (partial turchin53 jog-min jog-max -0.5 3.5))
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
