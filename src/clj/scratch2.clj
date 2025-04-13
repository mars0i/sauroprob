^:kindly/hide-code
(ns scratch
  (:require [clojure.math :as m] ; new in Clojure 1.11 
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.clay.v2.api :as clay] ; needed for clay eval keymappings
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.iterfreqs-fns :as ifn]
            ;[sauroprob.plotly :as sp]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(let [K 10000
      r 1.7]
  (ifn/plot-fns-with-cobweb {:x-max (* 1.5 K)
                             :fs [(um/logistic-contin-deriv K r)]
                             :labels [(str "r=" r)]
                             :init-x 25
                             :n-cobweb 21
                             ;:n-seq-iterates 300
                             ;:n-dist-iterates 1000
                             }))

(let [K 10000
      C  9500
      r 1.7]
  (ifn/plot-fns-with-cobweb {:x-max (* 1.5 K)
                             :fs [(um/williams-bossert-deriv K C r)]
                             :labels [(str "r=" r)]
                             :init-x 25
                             :n-cobweb 21
                             ;:n-seq-iterates 300
                             ;:n-dist-iterates 1000
                             }))
