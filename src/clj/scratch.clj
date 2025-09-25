^:kindly/hide-code
(ns scratch
  (:require [clojure.math :as m]
            [scicloj.kindly.v4.kind :as kind]
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


;; Not really about chaos
;; From Seger and Brockman 1987 p. 192 on bet hedging

;; Mixture of the two specialist strategies:

;(clojure.repl/dir clojure.math)

(defn mixed-expectation
  [p]
  (let [q (- 1 p)
        dry-specialist-in-dry p
        dry-specialist-in-wet (* q 0.6)
        wet-specialist-in-wet q
        wet-specialist-in-dry (* q 0.58)
        wet-sum (+ dry-specialist-in-wet wet-specialist-in-wet)
        dry-sum (+ wet-specialist-in-dry dry-specialist-in-dry)]
    (* 1/2 (+ wet-sum dry-sum))))

(defn mixed-variance
  [p]
  (let [q (- 1 p)
        dry-specialist-in-dry p
        dry-specialist-in-wet (* q 0.6)
        wet-specialist-in-wet q
        wet-specialist-in-dry (* q 0.58)
        wet-sum (+ dry-specialist-in-wet wet-specialist-in-wet)
        dry-sum (+ wet-specialist-in-dry dry-specialist-in-dry)
        wet-sum-sq (* wet-sum wet-sum)
        dry-sum-sq (* dry-sum dry-sum)]
    (- (* 1/2 (+ wet-sum-sq dry-sum-sq))
       (mixed-expectation p))))

(defn mixed-geom
  [p]
  (let [q (- 1 p)
        dry-specialist-in-dry p
        dry-specialist-in-wet (* q 0.6)
        wet-specialist-in-wet q
        wet-specialist-in-dry (* q 0.58)
        wet-sum (+ dry-specialist-in-wet wet-specialist-in-wet)
        dry-sum (+ wet-specialist-in-dry dry-specialist-in-dry)]
    (m/sqrt (* wet-sum dry-sum))))
