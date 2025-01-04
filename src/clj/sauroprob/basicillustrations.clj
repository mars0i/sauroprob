(ns sauroprob.core
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

  ;; Four moran1959 plots
  (def four-moran-specs [(sh/make-vl-spec 0.0 3.0 um/moran1950 1.5 1 [0.01] 10 :fixedpt-x 1.0)
                         (sh/make-vl-spec 0.0 3.0 um/moran1950 2.0 1 [0.01 0.1 0.8 2.0] 10 :fixedpt-x 1.0)
                         (sh/make-vl-spec 0.0 3.0 um/moran1950 2.5 2 [0.01] 10 :fixedpt-x 1.0)
                         (sh/make-vl-spec 0.0 3.0 um/moran1950 3.0 1 [0.01] 10 :fixedpt-x 1.0)])
  (def grid-spec (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 2 :CONCAT four-moran-specs))
  (oz/view! grid-spec)

  (def yo (sh/neg-one-line 0.0 1.0 (um/logistic 2.0) 0.5))

  ;; Checking whether apparent logistic fixed points are in fact:
  (um/logistic 2 0.5)
  (um/logistic 3 0.6667)
  (um/logistic 4 0.75)
  (um/logistic 4.5 0.77)

  ;; Four logistic plots
  (def four-logistic-specs [(sh/make-vl-spec 0.0 1.0 um/logistic 2.0 3 [0.01 0.1 0.2 0.7 0.8] 10 :fixedpt-x 0.3)
                            (sh/make-vl-spec 0.0 1.0 um/logistic 3.0 2 [0.01 0.5 0.9] 10 :fixedpt-x 0.66667)
                            (sh/make-vl-spec 0.0 1.0 um/logistic 4.0 1 [0.01 0.2 0.5 0.8] 10 :fixedpt-x 0.75 :addl-plots [(sh/horiz 1.0)])
                            (sh/make-vl-spec -0.2 1.2 um/logistic 4.5 1 [0.01] 10 :fixedpt-x 0.77 :addl-plots [(sh/horiz 1.0)])
                           ])
  (def grid-spec (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 2 :CONCAT four-logistic-specs))
  (oz/view! grid-spec)

  (oz/view! (sh/make-vl-spec -0.2 1.2 um/logistic 4.5 1 [0.3] 10 0.77)) ; note different x-min, x-max

  (def grid-spec (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 4 :CONCAT (concat four-moran-specs four-logistic-specs)))
  (oz/view! grid-spec)


  (def some-specs [(sh/make-vl-spec 0.0 3.0 um/moran1950 1.25 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 4.5 um/moran1950 1.5 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 4.5 um/moran1950 2.0 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 4.5 um/moran1950 2.5 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 4.5 um/moran1950 3.0 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 4.5 um/moran1950 3.5 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 4.5 um/moran1950 3.75 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 1.0 um/logistic 1.5 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 1.0 um/logistic 2.0 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 1.0 um/logistic 3.0 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 1.0 um/logistic 4.0 1 [0.01] 10)
                   (sh/make-vl-spec 0.0 1.2 um/logistic 4.5 1 [0.01] 10)]) ; note different x-max
  (def grid-spec (hc/xform sh/grid-chart :COLUMNS 3 :ROWS 4 :CONCAT some-specs))
  (oz/view! grid-spec)
)
