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

  (oz/view! (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 5 :CONCAT 
                      [(sh/make-vl-spec 0.0 3.0 um/moran1950 1.0  2 [] 0 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 1.1  2 [] 0 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 1.25 2 [] 0 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 1.5  2 [] 0 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 1.75 2 [] 0 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 1.9  2 [] 0 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 2.0  2 [] 0 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 2.25 2 [] 0 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 2.5  2 [] 0 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 2.7  2 [] 0 1.0)
                       (sh/make-vl-spec 0.0 3.0 um/moran1950 3.0  2 [] 0 1.0)
                      ]))

  ;; Four moran1959 plots
  (def four-moran-specs [(sh/make-vl-spec 0.0 3.0 um/moran1950 1.5 1 [0.01] 0 1.0)
                         (sh/make-vl-spec 0.0 3.0 um/moran1950 2.0 1 [0.01 0.1 0.8 2.0] 10 1.0)
                         (sh/make-vl-spec 0.0 3.0 um/moran1950 2.5 2 [0.01] 10 1.0)
                         (sh/make-vl-spec 0.0 3.0 um/moran1950 3.0 1 [0.01] 10 1.0)])
  (def grid-spec (hc/xform sh/grid-chart :COLUMNS 2 :ROWS 2 :CONCAT four-moran-specs))
  (oz/view! grid-spec)

)
