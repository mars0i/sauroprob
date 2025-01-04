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

  (oz/view! (sh/make-vl-spec 0.0 1.0 um/logistic 4.25 1
                             [0.05 0.075 0.15] 3
                             :fixedpt-x 0.5
                             :addl-plots [(sh/horiz 1.0)]))

  ;; fixing aspect ration:
  ;; Maybe something like `{:y scale = {domain = (200,300)}}` but the
  ;; correct syntax. cf. forage/viz/hanami.clj:
  ;;             :XSCALE {"domain" [data-bound-min data-bound-max]}
  ;;             :YSCALE {"domain" [data-bound-min data-bound-max]}
 

)
