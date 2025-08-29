;; Code inspired by Turchin, _Complex Population Dynamics_, 2003,
;; figure 5.3, p. 149. See p. 148 for partial description of function, parameters.

^:kindly/hide-code
(ns turchin-complexpopdyn-fig53
  (:require [clojure.math :as m]
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

(def $$ partial)

^:kindly/hide-code
;; Make LaTeX work in Plotly labels.
;; Side effects: Shifts labels in plotly, makes Clay not return to same position.
;(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

(defn turchin53
  "A modified Ricker function like the one that Turchin 2003 uses
  to generate figure 5.3.  jog-slope should usually be a small negative
  number.  This will be the slope of the 'jog', the region in which the
  Ricker function is replaced with a jog function.  jog-min and jog-max
  should be numbers near 1 s.t. 1 falls within (jog-min, jog-max). This is
  the interval in which the Ricker function will be replaced by the linear
  'jog' function. r is the parameter of a normalized Ricker map."
  [jog-fn jog-min jog-max r x]
  (if (and (> x jog-min)
           (< x jog-max))
    (jog-fn x)
    (um/normalized-ricker r x)))

(defn linear-jog
  [jog-slope]
  (fn [x]
    (+ (* jog-slope x)
       (- 1 jog-slope))))

(defn cube-jog
  [x]
  (- 1.25
     (m/pow (* 10 (- x 1))
            3)
     (/ x 4)))

;; I tried to move this later and forward declare it, but that doesn't seem to work.
(defn turchin53-linear
  "A modified Ricker function like the one that Turchin 2003 uses
  to generate figure 5.3.  jog-slope should usually be a small negative
  number.  This will be the slope of the 'jog', the region in which the
  Ricker function is replaced with a linear function.  jog-min and jog-max
  should be numbers near 1 s.t. 1 falls within (jog-min, jog-max). This is
  the interval in which the Ricker function will be replaced by the linear
  'jog' function. r is the parameter of a normalized Ricker map."
  [jog-slope jog-min jog-max r x]
  (turchin53 (linear-jog jog-slope) jog-min jog-max r x))

(defn turchin53-cubical
  [jog-min jog-max r x]
  (turchin53 cube-jog jog-min jog-max r x))

(def jog-width 0.1)
(def half-width (/ jog-width 2))
(def jog-min (- 1 half-width))
(def jog-max (+ 1 half-width))

(defn intermittent-drand
  "Returns a uniform random number between noise-min and noise-max using
  rand-fn and rng only when a random number in [0,1) is less than
  threshold."
  [threshold rand-fn rng noise-min noise-max]
  (if (< (fr/drandom rng) threshold)
    (rand-fn rng noise-min noise-max)
    0))

(defn noisy-fn
  [rand-fn rng jog-min jog-max f x]
  (let [noise-min (* jog-min 1.1)
        noise-max (* jog-max 1.1)]
  (+ (f x) 
     (rand-fn rng noise-min noise-max))))

;;---

;; Different init-x's allow the chaos end sooner, or later.  It just
;; depends how soon a value gets trapped in the basin of attraction
;; where the abs val of the slope is < 1..
(def common-params {:x-max 4.0
                    ;:init-x 1.01 ; works well for jog-width 0.01
                    :init-x 1.8075 ; works well for jog-width 0.1
                    :n-cobweb 30
                    ;; SOMETHING WEIRD GOING ON: When I change
                    ;; n-dist-iterates, it changes the sequence plot,
                    ;; apparently changing the evolution of the population.
                    :n-dist-iterates 280
                    :n-seq-iterates 380
                    ;:intro-label-md (str "$r=" 3.5 ":$") 
                   })

(def rng (fr/rng :well1024a 778914531))

;;---

;; This is an original normalized Ricker function without any modifications.
(let [f ($$ um/normalized-ricker 3.5)
      comps [1]]
  (fns/plots-grid (merge 
                    common-params
                    {:fs (map ($$ msc/n-comp f) comps)
                     :labels (map (fn [n] (str "$f^" n "$")) comps)
                     :n-dist-iterates 5000})))

;; This is uses `turchin53-cubical`, a Ricker function with a "jog" 
;; near (1,1). It's like a Ricker function with r=3.5, but near 1,
;; it's got a small negative slope based on a negative cubical function.
;; Because of this, the chaotic evolution eventually wanders into the
;; basin of (1,1) and stays there forever.
(let [f ($$ turchin53-cubical jog-min jog-max 3.5)
      comps [1]]
  (fns/plots-grid (merge 
                    common-params
                    {:fs (map ($$ msc/n-comp f) comps)
                     :labels (map (fn [n] (str "$f^" n "$")) comps)
                    })))

;; If you set the multipliers in the next function to make the min and max
;; small, it has a weird effect of making it quasi-cyclic.  Maybe because
;; you destroy sensitive dependence by making a point "regional"?

;; `turchin53-cubical` with noise.
;; Maybe the noise is kicking it back to the trap some of the time, and
;; then it has to restart the escape again?
(let [f ($$ noisy-fn fr/drandom rng jog-min jog-max ; (- 1 (* 1.5 half-width)) (+ 1 (* 1.5 half-width))
                 ($$ turchin53-cubical jog-min jog-max 3.5))
      comps [1]]
  (fns/plots-grid (merge 
                    common-params
                    {:fs (map ($$ msc/n-comp f) comps)
                     :labels (map (fn [n] (str "$f^" n "$")) comps)
                     :n-cobweb 40
                    })))

;; **QUESTION: Why do the fluctuations above have such a narrow range 
;; even when escaping the basin of (1,1)?**
;; See below for possible answers.

;; This is a uses `turchin53-linear`, a Ricker function with a "jog" near (1,1).
;; It's like a Ricker function with r=3.5, but near 1, it's linear with a small
;; negative slope. Note that the result is essentially the same as using
;; `turchin53-cubical`.
(let [f ($$ turchin53-linear -0.5 jog-min jog-max 3.5)
      comps [1]]
  (fns/plots-grid (merge 
                    common-params
                    {:fs (map ($$ msc/n-comp f) comps)
                     :labels (map (fn [n] (str "$f^" n "$")) comps)
                    })))

;; turchin53-linear with noise
(let [f ($$ noisy-fn fr/drandom rng jog-min jog-max ; (- 1 (* 1.5 half-width)) (+ 1 (* 1.5 half-width))
                 ($$ turchin53-linear -0.5 jog-min jog-max 3.5))
      comps [1]]
  (fns/plots-grid (merge 
                    common-params
                    {:fs (map ($$ msc/n-comp f) comps)
                     :labels (map (fn [n] (str "$f^" n "$")) comps)
                     :n-cobweb 40
                    })))

;; **Again, why do the fluctuations above have such a narrow range 
;; even when escaping the basin of (1,1)?**
;; See below for possible answers.


;; This one is like the random-enhanced turchin-cubical (or turchin-linear)
;; version above, but using intermittent random shocks rather than a random 
;; shock on each iteration. Seems like e.g. a Lévy distribution with the right
;; a μ value would work, too.
(let [f ($$ noisy-fn 
                 ($$ intermittent-drand 0.04 fr/drandom) rng jog-min jog-max
                 ($$ turchin53-cubical jog-min jog-max 3.5))
      comps [1]]
  (fns/plots-grid (merge 
                    common-params
                    {:fs (map ($$ msc/n-comp f) comps)
                     :labels (map (fn [n] (str "$f^" n "$")) comps)
                     :n-cobweb 40
                    })))

;; Comments on preceding experiment using `turchin53-cubical`:
;;
;; - I'm a little bit confused still about why the curve in the cobweb
;;   plot looks like it does.
;;
;; - Compared to the random-shocks-on-every-iteration examples,
;;   the preceding allows chaos to explore a wider range of x values.
;;
;; - On the other hand, the wide range is *sometimes* less wide than
;;   a system without random shocks.
;;
;; - Depending on the threshold value, random seed, and init-x, this
;;   setup both allows stability to persist for a long time before
;;   escape into chaos (as you'd expect) *and also* can send the system
;;   into stability before it would have without the random shocks.
;;
;;   This is illustrated by, for example, using these parameters:

(comment
  (def rng (fr/rng :well1024a 778914531))
  {:init-x 1.8075}
  ($$ intermittent-drand 0.04 "...")
)

;; - These points are compatible with my guess that the reason that
;;   constant noise doesn't allow wide variation in chaos is that the
;;   system keeps wandering back into the stable fixed point, and
;;   during chaotic periods is always in the process of climbing away
;;   from the fixed point before it gets sent back.  Not sure about that.
;;
;; - Seems like it should also work to use e.g. a Lévy distribution with
;;   a μ value that makes large hits rare.
