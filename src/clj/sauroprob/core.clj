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

  (oz/view! (sh/histogram 100 (take 10000 (iterate (um/logistic 4.0) 0.6))))

  (oz/view! (sh/make-vl-spec 0.0 1400 um/logistic-plus [1000 3.00] [1] [0.1] 40))
  

  (oz/view! (sh/vl-plot-seq "1K" (take 1000 (iterate (um/logistic-plus 100000 2.57) 0.1))))

  (oz/view! (sh/make-vl-spec 0.0 5.0 um/pre-ricker [0.3679] [1] [] 1))

  (oz/view! (sh/make-vl-spec 0.0 5.0 um/original-ricker [] [1] [] 1))


  (oz/view! (sh/make-vl-spec 0.0 1.0 um/logistic [3.0] [1] [0.99] 10))

  (oz/view! (sh/make-vl-spec 0.0 1.0 um/logistic [3.0] (msc/irange 1 6)
                             [0.05 0.075 0.15] 3
                             :fixedpt-x 0.5
                             :addl-plots [(sh/horiz 1.0)]))
  (require 'clojure.repl)
  (clojure.repl/pst)

)
