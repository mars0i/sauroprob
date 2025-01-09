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
  (def four-moran-specs [(sh/make-vl-spec 0.0 3.0 um/moran1950 1.5 1 [0.01] 0 :fixedpt-x 1.0)
                         (sh/make-vl-spec 0.0 3.0 um/moran1950 2.0 1 [0.01 0.1 0.8 2.0] 10 :fixedpt-x 1.0)
                         (sh/make-vl-spec 0.0 3.0 um/moran1950 2.5 2 [0.01] 3 :fixedpt-x 1.0)
                         (sh/make-vl-spec 0.0 3.0 um/moran1950 3.0 1 [0.01] 10 :fixedpt-x 1.0)])
  (def grid-spec (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 2 :CONCAT four-moran-specs))
  (oz/view! grid-spec)

  ;; Illustrates discussion on page 578
  (oz/view! (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 5 :CONCAT 
                      [(sh/make-vl-spec 0.0 3.0 um/moran1950 1.0  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 1.1  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 1.25 2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 1.5  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 1.75 2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 1.9  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 2.0  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 2.25 2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 2.5  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 2.7  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 3.0  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 3.5  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 4.0  2 [0.25] 4 :fixedpt-x 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 5.0  2 [0.25] 4 :fixedpt-x 1.0)
                      ]))

  (oz/view! (sh/make-vl-spec 0 4.5 um/moran1950 [3.5] 3 [3.4] 14 :fixedpt-x 1.0))

)

