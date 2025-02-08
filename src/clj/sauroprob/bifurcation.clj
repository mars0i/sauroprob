^:kindly/hide-code
;^{:kindly/options {:kinds-that-hide-code #{:kind/code}}}
;^{:kindly/options {:hide-code true}}
; cf. https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/Hiding.20all.20code.20in.20namespace
; and https://github.com/scicloj/clay/issues/202
(ns 
  sauroprob.bifurcation 
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.clay.v2.api :as clay] ; needed for clay eval keymappings
            [utils.math :as um]
            [sauroprob.hanami :as sh]))

;; ### Humps

;; Where do the additional humps come from in the iterations of the function?
;; The new humps are at locations of $x$ values such that two iterations of
;; $F$ map to the $x$ value for $F$'s hump.
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [1.95] [1 2] [0.086 1.572] 2))
; That is, the $y$ value of $F^2$: at those initial $x$'s is the $y$ value of the $F^1$ hump.
;; There are two humps in $F^2$ because there are two ways to get to the $F^1$
;; hump---since $F$ slopes down on both sides, there are two initial $x$'s
;; whose $y$ values under $F^1$ are the $x$ value under the $F^1$ hump.


;; ### Stability and bifurcation

;; When $F$ has $|slope| < 1$ at the fixed point, $F^2$ does as well, and since
;; $F$'s slope is $< -1$, $F^2$'s is less than 1, so it doesn't cross
;; $y=x$. Ditto for $F^4$, $F^8$, etc.
;; Similarly, $F^3$, $F^5$, $F^7$ have gentle curves to the right of the fixed
;; point, so they don't cross $y=x$.  And since $F^3$ has slope $< -1$, $F^6$ has
;; slope $< 1$ and doesn't cross $y=x$.
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [1.95] [1 3 6] [] 1 :fixedpt-x 1.0))

;; However, once $F$ has a slope > -1 at the fixed point, $F^2$ must have a slope < 1,
;; so it crosses $y=x$ below the fp, and since it's going up at a rate > 1 at the fp,
;; it crosses $y=x$ above the fp.  So now $F^2$ has four fp's (including 0).
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.20] [1 2] [] 1 :fixedpt-x 1.0))

;; Note, however, that $F^2$ crosses again near its extrema, so the slopes
;; at the new fps are gentle.
;; Thus this is not enough to make $F^4$ cross again.
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.20] [1 2 4] [] 1 :fixedpt-x 1.0))

;; And as long as $F^2$ has |slope| < 1 at the new fp's, the same is true of $F^4$, so there 
;; are no new crossings.
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.40] [1 2 4] [] 1 :fixedpt-x 3.33))
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.50] [1 2 4] [] 1 :fixedpt-x 3.43))

;; Although at the newer fp on the left, this reasoning doesn't seem to apply:
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.45] [1 2 4] [] 1 :fixedpt-x 0.055))

;; But when $F$ gets peaked enough that it causes $F^2$ to have a slope > -1 at the newer
;; right-hand fp, $F^4$ must have new crossings as well.
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.65] [1 2 4] [] 1 :fixedpt-x 3.52))

(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [3.10] [1 2 4 8] [] 1))

(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [3.10] [1 2 4 8 16] [] 1))

;; When the slope at a fp is not steep, subsequent powers just reduce the slope at that fp:
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.50] [1 2 4 32] [] 1))
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.50] [64] [] 1))

;; But when the slope is high, it just increases with subsequent powers:
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [3.10] [1 2 4 8] [] 1))

;; At that point, the higher powers will mostly have very steep crossings. 
;; I suppose that the only way you get a stable fp is when some higher power 
;; just happens to have a crossing near a peak or valley in its curve.
;; Here are two illustrations:
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [3.10] [16] [] 1))
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [3.10] [32] [] 1))

;; Some other examples:
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.65] [4 8 16] [] 1))
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.72] [8 16] [] 1))

;; ### More on bifurcation
;; Based on Devaney, 3rd ed., chapter 12.

(comment
  (require 'clojure.repl)
  (clojure.repl/pst)

  (require '[scicloj.clay.v2.api :as clay])
  (clay/make! {:source-path "src/clj/sauroprob/bifurcation.clj"
               :format [:quarto :html]}) ; opens on localhost:1971
  ;; Note Quarto has to be installed from the web (not as a Clojure
  ;; dependency) for this to work.
)
