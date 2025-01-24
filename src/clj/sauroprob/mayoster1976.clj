;; Plots illustrating ideas from May and Oster's "Bifurcations and dynamical
;; complexity in simple ecological models", _The American Naturalist_ 1976.
(ns sauroprob.mayoster1976
  (:require 
            ;[clojure.math.numeric-tower :as m]
            [clojure.math :as m] ; new in Clojure 1.11 
            [oz.core :as oz]
            [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]
            [utils.json :as json]
            [utils.string :as st]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.hanami :as sh]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(comment
  (oz/start-server!)

  ;; Illustration of methods.  Note that for this family of functions, the
  ;; last optional arg, which is the x coord of F^1 fixed point, is always
  ;; the same, 1.0.
  (def four-ricker-specs [(sh/make-vl-spec 0.0 3.0 um/real-ricker [1.5] 1 [0.01] 0 :fixedpt-x 1.0)
                          (sh/make-vl-spec 0.0 3.0 um/real-ricker [2.0] 1 [0.01 0.1 0.8 2.0] 10 :fixedpt-x 1.0)
                          (sh/make-vl-spec 0.0 3.0 um/real-ricker [2.5] 2 [0.01] 3 :fixedpt-x 1.0)
                          (sh/make-vl-spec 0.0 3.0 um/real-ricker [3.0] 1 [0.01] 10 :fixedpt-x 1.0)])
  (def grid-spec (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 2 :CONCAT four-ricker-specs))
  (oz/view! grid-spec)

  ;; Illustrates discussion on page 578
  (oz/view! (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 5 :CONCAT 
                      [(sh/make-vl-spec 0.0 3.0 um/real-ricker [1.0]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [1.1]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [1.25] 2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [1.5]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [1.75] 2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [1.9]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [2.0]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [2.25] 2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [2.5]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [2.7]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [3.0]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [3.5]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [4.0]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/real-ricker [5.0]  2 [0.25] 4 :fixedpt-x 1.0)
                      ]))

  (oz/view! (sh/make-vl-spec 0 4.5 um/real-ricker [3.5]      1 [0.5] 7))
  (oz/view! (sh/make-vl-spec 0 4.5 (um/real-ricker 3.5) []   1 [0.5] 7))
  ;; Why aren't the following producing results like the preceding?
  (oz/view! (sh/make-vl-spec 0 4500 um/ricker-relf [3.5 1000] 1 [0.5] 7))
  ;; check: should be same as preceding line:
  (oz/view! (sh/make-vl-spec 0 4.5 (um/ricker-relf 3.5 1000) [] 1 [0.5] 7))
  (oz/view! (sh/make-vl-spec 0 4.5 um/floored-ricker-relf [3.5 1000] 1 [0.5] 7))

  ;; Illustrate mapping path for a composed function:
  (oz/view! (sh/make-vl-spec 0 7 (msc/n-comp (um/real-ricker 2.5) 5) [] 1 [6] 12 :fixedpt-x 1.0))
  (oz/view! (sh/make-vl-spec 0 7 um/real-ricker [2.5] 3 [3.4] 14 :fixedpt-x 1.0))

  ;; Run the step iterations on F^3, but also print F via all-plots, to
  ;; show that the third step goes to the max of F.  But it doesn't work
  ;; precisely?  Is this image slop?
  (oz/view! (let [x-max 4.5
                  param 2.5]
              (sh/make-vl-spec 0 x-max  ; domain boundaries
                               (msc/n-comp (um/real-ricker param) 4) ; initial curve fn
                               [] ; parameters for curve fn
                               1  ; number of compositions of curve fn
                               [1.2] 20 ; initial x's and number of steps
                               :fixedpt-x 1.0
                               :addl-plots (sh/make-fn-vl-specs 0 x-max um/real-ricker [param] 1))))

  ;; Similar experiment for the 6th composition.  Not looking good.
  ;; Getting stuck in a fixedpoint.  (Float slop??)
  (oz/view! (let [x-max 8
                  param 2.5]
              (sh/make-vl-spec 0 x-max  ; domain boundaries
                               (msc/n-comp (um/real-ricker param) 6) ; initial curve fn
                               [] ; parameters for curve fn
                               1  ; number of compositions of curve fn
                               [3.6] 7 ; initial x's and number of steps
                               :fixedpt-x 1.0
                               :addl-plots (sh/make-fn-vl-specs 0 x-max um/real-ricker [param] 1))))

  ;; This illustrates the expected result that n steps on F is equivalent to one step on F^n:
  (let [x-max 3.5
        param 2.5
        init-x 0.22
        num-comps 3]
    (oz/view!
      (hc/xform sh/grid-chart :COLUMNS 1 :ROWS 2 :CONCAT 
                ;; Plot single-step path on F^n, but also plot F^1:
                [(sh/make-vl-spec 0 x-max  ; domain boundaries
                                  (msc/n-comp (um/real-ricker param) num-comps) ; initial curve fn
                                  [] ; parameters for curve fn
                                  1  ; number of compositions of curve fn
                                  [init-x] 1 ; initial x's and number of steps
                                  :fixedpt-x 1.0
                                  ;; Add plot for F^1:
                                  :addl-plots (sh/make-fn-vl-specs 0 x-max um/real-ricker [param] 1))
                 ;; Plot n-comp steps on F, but also plot F^n:
                 (sh/make-vl-spec 0 x-max  ; domain boundaries
                                  um/real-ricker ; initial curve fn
                                  [param] ; parameters for curve fn
                                  num-comps  ; number of compositions of curve fn
                                  [init-x] num-comps ; initial x's and number of steps
                                  :fixedpt-x 1.0)])))

  (oz/start-server!)

)

