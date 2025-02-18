;; # Experiments to go with Devaney 3rd ed, ch. 12
;; ---
^:kindly/hide-code
(ns sauroprob.devaney12
  (:require [clojure.math :as m]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.clay.v2.api :as clay] ; needed for clay eval keymappings
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.hanami :as sh]))

;; ## $\S 12.1$: $x_{n+1} = \lambda e^x$

;; ### $\lambda < 0$,  $E_\lambda^2$

;; Note $E'_\lambda (x) = (\lambda e^x)' = \lambda e^x$, and 
;; $(F_\lambda^2)' = (\lambda e^{\lambda e^x})' = \lambda e^x \lambda e^{\lambda e^x}$. 

;; $E_\lambda^2$:
(defn scaled-exp-2
  ([lambda] (partial scaled-exp-2 lambda))
  ([lambda x] (um/scaled-exp lambda (um/scaled-exp lambda x))))

(defn second-deriv-scaled-exp-2
  "Implements the second derivative of the composition of lambda exp(x)
  with itself."
  ([lambda] (partial second-deriv-scaled-exp-2 lambda))
  ([lambda x]
  (* (um/scaled-exp lambda x)
     (scaled-exp-2 lambda x)
     (+ 1 (um/scaled-exp lambda x)))))

;; #### $\lambda < -e$ (i.e. $\lambda = -e - 3$), $E_\lambda^2$

;; The $E_\lambda^2$ in next few plots all display the same $E_\lambda^2$,
;; but the first plot uses a pre-composed $E_\lambda^2$, so that's what the 
;; mapping lines follow.  In the other plots, mapping lines are run through $E_\lambda$.

(kind/vega-lite (sh/make-vl-spec [-7.0 2.0] scaled-exp-2 [(- (- m/E) 3.0)] [1] [-2 -1] 5 :y-lims [-7.0 1.0]))
;; These illustrate the approach to the 2-cycle:
(kind/vega-lite (sh/make-vl-spec [-6.0 0.5] um/scaled-exp [(- (- m/E) 3.0)] [1 2] [-2] 9 :y-lims [-7.0 1.0]))
(kind/vega-lite (sh/make-vl-spec [-6.0 0.5] um/scaled-exp [(- (- m/E) 3.0)] [1 2] [-1] 9 :y-lims [-7.0 1.0]))
;; These illustrate the 2-cycle (initial $x$ determined by trial and error):
(kind/vega-lite (sh/make-vl-spec [-6.0 0.5] um/scaled-exp [(- (- m/E) 3.0)] [1 2] [-0.021178] 4 :y-lims [-7.0 1.0]))

;; ?:
;; Let $\lambda = -(e + 3) = -5.718281828459045$, and try $\hat{x} = -0.021178$.

;; So $-0.021178 \approx -5.718 e^{-5.718 e^{-0.021178}}$

;; Is this true?  Let's see:

(let [λ -5.718281828459045
      x-hat -0.021178]
  (* λ (m/exp (* λ (m/exp x-hat)))))

;; So this is one fixed point of $E_\lambda^2$.  The other one on the
;; 2-cycle should be approximately $E_\lambda(\hat{x}):$

(let [λ -5.718281828459045
      x-hat -0.021178]
  (* λ (m/exp x-hat)))

;; Let's try starting the mapping at that point:
(kind/vega-lite (sh/make-vl-spec [-7.0 0.5] um/scaled-exp [(- (- m/E) 3.0)] [1] [-5.598453397779256] 4 :y-lims [-7.0 1.0]))

;; To summarize, for $\lambda = -5.718281828459045$, $E_\lambda$'s fixed
;; point is unstable.  Given precisely the value of the fixed point, it
;; will remain there, but any other point will walk toward one of
;; $E_\lambda^2$'s fixed points.  When (maybe in the limit) a value gets
;; to one of those $E_\lambda^2$ fixed points, subsequently $E_\lambda$ will
;; generate a 2-cycle between the two $E_\lambda^2$ fixed points.  That's
;; the first bifurcation.

;; ---

;; This illustrates the point on Devaney p. 99 that $E_\lambda^2$ (blue) is convex
;; down where $E_\lambda > -1$ (orange), and convex up where $E_\lambda >
;; -1$.  The upper curve is $(E_\lambda^2)''$.  (Ignore the fixed point;
;; it's not relevant.)  Notice where $E_\lambda$ crosses the $y=-1$ line,
;; and where $(E_\lambda^2)''$ crosses the $y$-axis, $y=0$. See
;; math/dynamicalsysts/DevaneyIntroChaoticDynSystsPage99.md for more, and
;; math/dynamicalsysts/lambdaequals_ex-*.gcx.

(let [lambda (- (- m/E) 2.0)] (kind/vega-lite (sh/make-vl-spec [-7.0 0.5] um/scaled-exp [lambda] [1 2] [] 1 :y-lims [-7.0 1.0] :addl-plots [((sh/make-one-fn-vl-spec-fn -7.0 0.5 second-deriv-scaled-exp-2 [lambda]) 1)])))

;; ---

;; #### $-e < \lambda < 0$ (i.e. $\lambda = -e + 2$), $E_\lambda^2$:
(kind/vega-lite (sh/make-vl-spec [-1.4 1.0] um/scaled-exp [(+ (- m/E) 2.0)] [1 2] [] 5 :y-lims [-0.8 -0.0]))
(kind/vega-lite (sh/make-vl-spec [-1.4 1.0] scaled-exp-2 [(+ (- m/E) 2.0)] [1] [-1.4 0.20] 5 :y-lims [-0.8 -0.0]))

;; ---

;; #### $\lambda = -e$, $E_\lambda^2$
(kind/vega-lite (sh/make-vl-spec [-4.0 1.0] um/scaled-exp [(- m/E)] [1 2] [] 1 :y-lims [-4.0 1.0]))

^:kindly/hide-code
(comment
  (kind/vega-lite (sh/make-vl-spec [-3.5 1.0] scaled-exp-2 [(- m/E)] [1] [] 1 :y-lims [-3.0 1.0]))
)

;; ---


;; ### $\lambda < 0$

;; #### $-e < \lambda < 0$, i.e. $\lambda = -e + 2$
(kind/vega-lite (sh/make-vl-spec [-1.4 0.0] um/scaled-exp [(+ (- m/E) 2.0)] [1] [-0.6 -0.3] 5 :y-lims [-0.8 -0.0]))


;; ### $\lambda < -e$, i.e. $\lambda = -e - 2$
(kind/vega-lite (sh/make-vl-spec [-2.0 -0.4] um/scaled-exp [(- (- m/E) 2.0)] [1] [-1.4 -1.1] 5 :y-lims [-3.5 0.0]))

;; #### $\lambda = -e$
;; These examples show how slow the approach to equilibrium is for $\lambda = -e$.
;; At $-e$, the slope is $-1$, so it's neither attracting nor repelling,
;; but because of the concavity, i.e. $f''<0$, at a distance from the fixed
;; point, it's as if the slope were $< -1$.
(kind/vega-lite (sh/make-vl-spec [-2.0 0.5] um/scaled-exp [(- m/E)] [1] [-1.5] 20 :y-lims [-2.0 0.0]))
(kind/vega-lite (sh/make-vl-spec [-2.0 0.5] um/scaled-exp [(- m/E)] [1] [-0.5] 10 :y-lims [-2.0 0.0]))
(kind/vega-lite (sh/make-vl-spec [-2.0 0.5] um/scaled-exp [(- m/E)] [1] [-1.10] 100 :y-lims [-2.0 0.0]))
(kind/vega-lite (sh/make-vl-spec [-2.0 0.5] um/scaled-exp [(- m/E)] [1] [-0.90] 100 :y-lims [-2.0 0.0]))



;; ---
;; ### $\lambda \geq 0$

;; #### $\lambda = e^{-1}$
(kind/vega-lite (sh/make-vl-spec [-4.0 2.0] um/scaled-exp [(/ m/E)] [1] [-0.5 1.70] 2 :y-lims [0.0 2.0]))

;; #### $\lambda < e^{-1}$ (i.e. $\lambda = e^{-1} - 0.1$)
(kind/vega-lite (sh/make-vl-spec [-4.0 2.0] um/scaled-exp [(- (/ m/E) 0.1)] [1] [-0.9 1.85] 6 :y-lims [0.0 2.0]))
(kind/vega-lite (sh/make-vl-spec [-4.0 3.5] um/scaled-exp [(- (/ m/E) 0.1)] [1] [2.07] 5 :y-lims [0.0 8.0]))

;; #### $\lambda > e^{-1}$ (i.e. $\lambda = e^{-1} + 0.1$)
(kind/vega-lite (sh/make-vl-spec [-4.0 2.0] um/scaled-exp [(+ (/ m/E) 0.1)] [1] [0.2] 6 :y-lims [0.0 2.0]))


^:kindly/hide-code
(comment
  (require 'clojure.repl)
  (clojure.repl/pst)
)


^:kindly/hide-code
(comment
;; obsolete:
;; What are the fixed points of $E_\lambda^2$?
;; For $\lambda < -e$, $\hat{x} = E_\lambda^2(\hat{x}) = 
;; \lambda e^{\lambda e^\hat{x}} = \lambda \exp(\lambda \exp\hat{x})$.
;; Suppose $\lambda=-3$.  Then 
;; $\hat{x} =  -3 e^{-3 e^\hat{x}}$ and $\ln \hat{x} =  \ln(-3) + -3 e^\hat{x}$.
;; So $\ln(-3) = \ln\hat{x} + 3e^\hat{x}$. Um $\ln(-3)$ is complex.  Oops.  Don't do this.
)
