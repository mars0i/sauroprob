^:kindly/hide-code
(ns sauroprob.core
  (:require [clojure.math :as m] ; new in Clojure 1.11 
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.clay.v2.api :as clay] ; needed for clay eval keymappings
            ;[oz.core :as oz]
            ;[aerial.hanami.common :as hc]
            ;[aerial.hanami.templates :as ht]
            ;[utils.json :as json]
            ;[utils.string :as st]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.hanami :as sh]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(kind/vega-lite (sh/make-vl-spec 0.0 3.0 um/normalized-ricker [2.5] [1] [0.6] 60))
(kind/vega-lite (sh/make-vl-spec 0.0 3.0 (msc/n-comp (um/normalized-ricker 2.5) 10) [] [1] [0.6] 20))
(kind/vega-lite (sh/vl-plot-seq "1K" (take 90 (iterate (um/normalized-ricker 2.5) 0.6))))
(kind/vega-lite (sh/histogram 100 (take 10000 (iterate (um/normalized-ricker 2.5) 0.6))))


(comment
  (kind/vega-lite (sh/histogram 100 (take 10000 (iterate (um/logistic 4.0) 0.6))))
  (kind/vega-lite (sh/make-vl-spec 0.0 1400 um/logistic-plus [1000 3.00] [1] [0.1] 40))

  (kind/vega-lite (sh/vl-plot-seq "1K" (take 1000 (iterate (um/logistic-plus 100000 2.57) 0.1))))

  (kind/vega-lite (sh/make-vl-spec 0.0 5.0 um/pre-ricker [0.3679] [1] [] 1))

  (kind/vega-lite (sh/make-vl-spec 0.0 5.0 um/original-ricker [] [1] [] 1))


  (kind/vega-lite (sh/make-vl-spec 0.0 1.0 um/logistic [3.0] [1] [0.99] 10))

  (kind/vega-lite (sh/make-vl-spec 0.0 1.0 um/logistic [3.0] (msc/irange 1 6)
                             [0.05 0.075 0.15] 3
                             :fixedpt-x 0.5
                             :addl-plots [(sh/horiz 1.0)]))
)

(comment
  (require 'clojure.repl)
  (clojure.repl/pst)
)
