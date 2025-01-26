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
  (def four-ricker-specs [(sh/make-vl-spec 0.0 3.0 um/normalized-ricker [1.5] 1 [0.01] 0 :fixedpt-x 1.0)
                          (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.0] 1 [0.01 0.1 0.8 2.0] 10 :fixedpt-x 1.0)
                          (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.5] 2 [0.01] 3 :fixedpt-x 1.0)
                          (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [3.0] 1 [0.01] 10 :fixedpt-x 1.0)])
  (def grid-spec (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 2 :CONCAT four-ricker-specs))
  (oz/view! grid-spec)

  ;; Illustrates discussion on page 578
  (oz/view! (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 5 :CONCAT 
                      [(sh/make-vl-spec 0.0 3.0 um/normalized-ricker [1.0]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [1.1]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [1.25] 2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [1.5]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [1.75] 2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [1.9]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.0]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.25] 2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.5]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.7]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [3.0]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [3.5]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [4.0]  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [5.0]  2 [0.25] 4 :fixedpt-x 1.0)
                      ]))

  (def real-spec (sh/make-vl-spec 0 4.5 um/normalized-ricker [3.5] 1 [0.5] 10))
  (oz/view! real-spec)
  ;; Note that the range for both the function and plotting has to be shifted to 
  ;; K-scale [0,4500] since K is 1000; init-x and fixed point must larger, too:
  (def rick-spec  (sh/make-vl-spec 0 4500 um/ricker         [1000 3.5] 1 [500] 10))
  (oz/view! rick-spec)
  (def floor-spec (sh/make-vl-spec 0 4500 um/floored-ricker [1000 3.5] 1 [500] 10))
  (oz/view! floor-spec)

  ;; A strategy for starting with pop size but producing a normalized-ricker output.
  ;; (Note this can't simply be built into a ricker fn, since params like
  ;; x-max are only used in make-vl-spec.  I'd need a wrapper around
  ;; make-vl-spec, I guess, to automate this kind of thing.)
  (let [K 1000.0
        N 500.0
        n-min 0.0
        n-max 4500.0
        X (/ K N)
        x-min (/ n-min K)
        x-max (/ n-max K)]
    (oz/view! (sh/make-vl-spec x-min x-max um/normalized-ricker [3.5] 1 [X] 10)))

  (oz/view! (sh/make-vl-spec 0 250 um/ricker          [2.9 100] 1 [90 85] 8 :fixedpt-x 100))
  (oz/view! (sh/make-vl-spec 0 250 um/floored-ricker  [2.9 100] 1 [90 85] 8 :fixedpt-x 100))

  ;; These don't plot correctly because the input needs to be on the K
  ;; scale (e.g. [0,4500], but the plot needs to be on the X scale [0,4.5]:
  (def rest-spec (sh/make-vl-spec 0 4.5 um/restored-ricker [3.5 1000] 1 [] 7))
  (oz/view! rest-spec)
  (def relf-spec (sh/make-vl-spec 0 4500 um/ricker-relf [3.5 1000] 1 [] 7))
  (oz/view! relf-spec)
  (oz/view! (sh/make-vl-spec 0 4.5 um/floored-ricker-relf [3.5 1000] 1 [0.5] 7))

  ;; Illustrate mapping path for a composed function:
  (oz/view! (sh/make-vl-spec 0 7 (msc/n-comp (um/normalized-ricker 2.5) 5) [] 1 [6] 12 :fixedpt-x 1.0))
  (oz/view! (sh/make-vl-spec 0 7 um/normalized-ricker [2.5] 3 [3.4] 14 :fixedpt-x 1.0))

  ;; Run the step iterations on F^3, but also print F via all-plots, to
  ;; show that the third step goes to the max of F.  But it doesn't work
  ;; precisely?  Is this image slop?
  (oz/view! (let [x-max 4.5
                  param 2.5]
              (sh/make-vl-spec 0 x-max  ; domain boundaries
                               (msc/n-comp (um/normalized-ricker param) 4) ; initial curve fn
                               [] ; parameters for curve fn
                               1  ; number of compositions of curve fn
                               [1.2] 20 ; initial x's and number of steps
                               :fixedpt-x 1.0
                               :addl-plots (sh/make-fn-vl-specs 0 x-max um/normalized-ricker [param] 1))))

  ;; Similar experiment for the 6th composition.  Not looking good.
  ;; Getting stuck in a fixedpoint.  (Float slop??)
  (oz/view! (let [x-max 8
                  param 2.5]
              (sh/make-vl-spec 0 x-max  ; domain boundaries
                               (msc/n-comp (um/normalized-ricker param) 6) ; initial curve fn
                               [] ; parameters for curve fn
                               1  ; number of compositions of curve fn
                               [3.6] 7 ; initial x's and number of steps
                               :fixedpt-x 1.0
                               :addl-plots (sh/make-fn-vl-specs 0 x-max um/normalized-ricker [param] 1))))

  ;; This illustrates the expected result that n steps on F is equivalent to one step on F^n:
  (let [x-max 3.5
        param 2.5
        init-x 0.22
        num-comps 3]
    (oz/view!
      (hc/xform sh/grid-chart :COLUMNS 1 :ROWS 2 :CONCAT 
                ;; Plot single-step path on F^n, but also plot F^1:
                [(sh/make-vl-spec 0 x-max  ; domain boundaries
                                  (msc/n-comp (um/normalized-ricker param) num-comps) ; initial curve fn
                                  [] ; parameters for curve fn
                                  1  ; number of compositions of curve fn
                                  [init-x] 1 ; initial x's and number of steps
                                  :fixedpt-x 1.0
                                  ;; Add plot for F^1:
                                  :addl-plots (sh/make-fn-vl-specs 0 x-max um/normalized-ricker [param] 1))
                 ;; Plot n-comp steps on F, but also plot F^n:
                 (sh/make-vl-spec 0 x-max  ; domain boundaries
                                  um/normalized-ricker ; initial curve fn
                                  [param] ; parameters for curve fn
                                  num-comps  ; number of compositions of curve fn
                                  [init-x] num-comps ; initial x's and number of steps
                                  :fixedpt-x 1.0)])))

  (oz/start-server!)

)

(comment
  (require '[clojure.math :as m])
  (require '[oz.core :as oz])
  (require '[aerial.hanami.common :as hc])
  (require '[aerial.hanami.templates :as ht])
  (require '[utils.json :as json])
  (require '[utils.string :as st])
  (require '[utils.misc :as msc])
  (require '[utils.math :as um])
  (require '[sauroprob.hanami :as sh])
)
