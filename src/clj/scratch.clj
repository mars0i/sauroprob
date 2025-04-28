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

(let [K 1000
      r 3.0143
      f (um/ricker K r)
      ceiled-f (um/ceiled f)
      floored-f (um/ceiled f)
      x-max (* K 2)
      init-x 0.001
      n-cobweb 30
      n-seq-iterates 200
      n-dist-iterates 10000
      comps [1]]
(kind/fragment [
  (fns/plots-grid {:x-max x-max
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "ricker^" n)) comps) ; removed LaTeX seem mwe5.clj
                   :init-x init-x
                   :n-cobweb n-cobweb
                   :n-seq-iterates n-seq-iterates
                   :n-dist-iterates n-dist-iterates})
  (fns/plots-grid {:x-max x-max
                   :fs (map (partial msc/n-comp ceiled-f) comps)
                   :labels (map (fn [n] (str "ceiled^" n)) comps) ; removed LaTeX seem mwe5.clj
                   :init-x init-x
                   :n-cobweb n-cobweb
                   :n-seq-iterates n-seq-iterates
                   :n-dist-iterates n-dist-iterates})
  (fns/plots-grid {:x-max x-max
                   :fs (map (partial msc/n-comp floored-f) comps)
                   :labels (map (fn [n] (str "floored^" n)) comps) ; removed LaTeX seem mwe5.clj
                   :init-x init-x
                   :n-cobweb n-cobweb
                   :n-seq-iterates n-seq-iterates
                   :n-dist-iterates n-dist-iterates})
 ]))


