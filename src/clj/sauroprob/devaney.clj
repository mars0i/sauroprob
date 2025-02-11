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

;; ### $x_{n+1} = \lambda e^x$


;; #### $\lambda \geq 0$:

;; $\lambda = e^{-1}$:
(kind/vega-lite (sh/make-vl-spec -4.0 2.0 um/scaled-exp [(/ m/E)] [1] [-0.5 1.70] 2 :y-lims [0.0 2.0]))

;; $\lambda < e^{-1}$ ($\lambda = e^{-1} - 0.1$):
(kind/vega-lite (sh/make-vl-spec -4.0 2.0 um/scaled-exp [(- (/ m/E) 0.1)] [1] [-0.9 1.85] 6 :y-lims [0.0 2.0]))
(kind/vega-lite (sh/make-vl-spec -4.0 3.5 um/scaled-exp [(- (/ m/E) 0.1)] [1] [2.07] 5 :y-lims [0.0 8.0]))

;; $\lambda > e^{-1}$ ($\lambda = e^{-1} + 0.1$):
(kind/vega-lite (sh/make-vl-spec -4.0 2.0 um/scaled-exp [(+ (/ m/E) 0.1)] [1] [0.2] 6 :y-lims [0.0 2.0]))


;; #### $\lambda < 0$:

;; This shows the approach to equilibrium for $\lambda = e$:
(kind/vega-lite (sh/make-vl-spec -2.0 0.5 um/scaled-exp [(- m/E)] [1] [-1.5] 20 :y-lims [-2.0 0.0]))
(kind/vega-lite (sh/make-vl-spec -2.0 0.5 um/scaled-exp [(- m/E)] [1] [-0.5] 10 :y-lims [-2.0 0.0]))

;; This shows how slow the approach is near the equilibrium for $\lambda = e$:
(kind/vega-lite (sh/make-vl-spec -2.0 0.5 um/scaled-exp [(- m/E)] [1] [-1.10] 100 :y-lims [-2.0 0.0]))
(kind/vega-lite (sh/make-vl-spec -2.0 0.5 um/scaled-exp [(- m/E)] [1] [-0.90] 100 :y-lims [-2.0 0.0]))

^:kindly/hide-code
(comment
  (require 'clojure.repl)
  (clojure.repl/pst)
)
