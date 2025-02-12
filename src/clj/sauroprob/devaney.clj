;; # Experiments to go with Devaney 3rd ed
;; ---
^:kindly/hide-code
(ns sauroprob.devaney
  (:require [clojure.math :as m]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.clay.v2.api :as clay] ; needed for clay eval keymappings
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.hanami :as sh]))

;; ## $\S 12.1$ $x_{n+1} = \lambda e^x$

;; ### $\lambda < 0$:

;; #### $-e < \lambda < 0$ (i.e. $\lambda = -e + 2$):
(kind/vega-lite (sh/make-vl-spec [-1.4 0.0] um/scaled-exp [(+ (- m/E) 2.0)] [1] [-0.6 -0.3] 5 :y-lims [-0.8 -0.0]))

;; ## $\lambda < -e$ (i.e. $\lambda = -e - 2$):
(kind/vega-lite (sh/make-vl-spec [-2.0 -0.4] um/scaled-exp [(- (- m/E) 2.0)] [1] [-1.4 -1.1] 5 :y-lims [-3.5 0.0]))

;; #### $\lambda = -e$:
;; These examples show how slow the approach to equilibrium is for $\lambda = -e$.
;; At $-e$, the slope is $-1$, so it's neither attracting nor repelling,
;; but because of the concavity, i.e. $f''<0$, at a distance from the fixed
;; point, it's as if the slope were $< -1$.
(kind/vega-lite (sh/make-vl-spec [-2.0 0.5] um/scaled-exp [(- m/E)] [1] [-1.5] 20 :y-lims [-2.0 0.0]))
(kind/vega-lite (sh/make-vl-spec [-2.0 0.5] um/scaled-exp [(- m/E)] [1] [-0.5] 10 :y-lims [-2.0 0.0]))
(kind/vega-lite (sh/make-vl-spec [-2.0 0.5] um/scaled-exp [(- m/E)] [1] [-1.10] 100 :y-lims [-2.0 0.0]))
(kind/vega-lite (sh/make-vl-spec [-2.0 0.5] um/scaled-exp [(- m/E)] [1] [-0.90] 100 :y-lims [-2.0 0.0]))


;; ### $\lambda < 0$,  $E_\lambda^2$:

;; #### $-e < \lambda < 0$ (i.e. $\lambda = -e + 2$):, $E_\lambda^2$:
(kind/vega-lite (sh/make-vl-spec [-1.4 1.0] um/scaled-exp [(+ (- m/E) 2.0)] [2] [] 5 :y-lims [-0.8 -0.0]))

;; #### $\lambda < -e$ (i.e. $\lambda = -e - 3$), $E_\lambda^2$::
(kind/vega-lite (sh/make-vl-spec [-7.0 2.0] um/scaled-exp [(- (- m/E) 3.0)] [2] [] 5 :y-lims [-6.0 1.0]))

;; #### $\lambda = e^{-1}$, $E_\lambda^2$:
(kind/vega-lite (sh/make-vl-spec [-3.5 1.0] um/scaled-exp [(- m/E)] [2] [] 1 :y-lims [-3.0 1.0]))


;; ---
;; ### $\lambda \geq 0$:

;; #### $\lambda = e^{-1}$:
(kind/vega-lite (sh/make-vl-spec [-4.0 2.0] um/scaled-exp [(/ m/E)] [1] [-0.5 1.70] 2 :y-lims [0.0 2.0]))

;; #### $\lambda < e^{-1}$ (i.e. $\lambda = e^{-1} - 0.1$):
(kind/vega-lite (sh/make-vl-spec [-4.0 2.0] um/scaled-exp [(- (/ m/E) 0.1)] [1] [-0.9 1.85] 6 :y-lims [0.0 2.0]))
(kind/vega-lite (sh/make-vl-spec [-4.0 3.5] um/scaled-exp [(- (/ m/E) 0.1)] [1] [2.07] 5 :y-lims [0.0 8.0]))

;; #### $\lambda > e^{-1}$ (i.e. $\lambda = e^{-1} + 0.1$):
(kind/vega-lite (sh/make-vl-spec [-4.0 2.0] um/scaled-exp [(+ (/ m/E) 0.1)] [1] [0.2] 6 :y-lims [0.0 2.0]))


^:kindly/hide-code
(comment
  (require 'clojure.repl)
  (clojure.repl/pst)
)
