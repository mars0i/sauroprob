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

  (oz/view! (sh/make-vl-spec 0.0 1.0 um/logistic 3.00 6
                             [0.05 0.075 0.15] 3
                             :fixedpt-x 0.5
                             :addl-plots [(sh/horiz 1.0)]))

  (def abs-spec (sh/make-vl-spec 0.0 1.0 um/tent 0.5 1 [] 3))
  (oz/view! abs-spec)

  (oz/view! (sh/make-vl-spec 0 4.5 um/foo 2.5 3 [] 14 :fixedpt-x 1.0))

)
