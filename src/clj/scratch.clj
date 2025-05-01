^:kindly/hide-code
(ns scratch
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [fastmath.random :as fr]
            [fastmath.stats :as fs]
            [tablecloth.api :as tc]
            ;[clojisr.v1.r :as R]
            ;[criterium.core :as crit]
            [utils.math :as um]
            [utils.misc :as msc]
            [sauroprob.iterfreqs-fns :as fns]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as ifn]))

;; Experiments illustrating what happens when ricker is ceiled or floored.
;; (Note that you'd think that floor could go immediately to a fixed point 
;; of 0 or K, but it depends on what the second value is.)

(let [K3     1000
      K4    10000
      K5   100000 ; 100K
      K6  1000000 ; 1M
      K7 10000000 ; 10M
      r 3.0143
      ceiled-f-3 (um/ceiled (um/ricker K3 r))
      ceiled-f-4 (um/ceiled (um/ricker K4 r))
      ceiled-f-5 (um/ceiled (um/ricker K5 r))
      ceiled-f-6 (um/ceiled (um/ricker K6 r))
      ceiled-f-7 (um/ceiled (um/ricker K7 r))
      x-max-3 (* K3 2)
      x-max-4 (* K4 2)
      x-max-5 (* K5 2)
      init-x 0.901
      n-cobweb 30
      n-seq-iterates 200
      n-dist-iterates 2000
      comps [1]]
  (kind/fragment [(comment
                  (fns/plots-grid {:x-max x-max-3
                                   :fs (map (partial msc/n-comp ceiled-f-3) comps)
                                   :labels (map (fn [n] (str "ceiled^" n)) comps)
                                   :init-x init-x
                                   :n-cobweb n-cobweb
                                   :n-seq-iterates n-seq-iterates
                                   :n-dist-iterates n-dist-iterates})
                  )
                  (fns/plot-iterates-histogram (take n-dist-iterates (iterate ceiled-f-7 init-x)))
                  ;; This makes Java go kerfluey, even with f-3:
                  ;(fns/plot-cdf (take n-dist-iterates (iterate ceiled-f-3 init-x))
                  ;              x-max-4 0)
                  (comment
                    (fns/plots-grid {:x-max x-max-4
                                     :fs (map (partial msc/n-comp ceiled-f-4) comps)
                                     :labels (map (fn [n] (str "ceiled^" n)) comps)
                                     :init-x init-x
                                     :n-cobweb n-cobweb
                                     :n-seq-iterates n-seq-iterates
                                     :n-dist-iterates n-dist-iterates})
                  )
                  (comment
                    (fns/plots-grid {:x-max x-max-5
                                     :fs (map (partial msc/n-comp ceiled-f-5) comps)
                                     :labels (map (fn [n] (str "ceiled^" n)) comps)
                                     :init-x init-x
                                     :n-cobweb n-cobweb
                                     :n-seq-iterates n-seq-iterates
                                     :n-dist-iterates n-dist-iterates})
                  )
                  ]))

