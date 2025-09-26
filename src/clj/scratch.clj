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
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))


;; Not really about chaos
;; From Seger and Brockman 1987 p. 192 on bet hedging

;; Mixture of the two specialist strategies:

;(clojure.repl/dir clojure.math)

(comment
  ;; wet specialist:
  (/ (+ 1 0.58) 2) ; arithmetic 
  (m/pow (* 1 0.58) 1/2) ; geometric
  ;; dry specialist:
  (/ (+ 1 0.6) 2) ; arithmetic
  (m/pow (* 1 0.6) 1/2) ; geometric
  )

(def wet-in-wet-fit 1)
(def wet-in-dry-fit 0.58)
(def dry-in-wet-fit 0.6)
(def dry-in-dry-fit 1)

(def wet-env-relf 1/2)
(def dry-env-relf (- 1 wet-env-relf))

(defn wetdry-pop-summary
  [summary-fn dry-specialist-relf]
  (let [wet-specialist-relf (- 1 dry-specialist-relf)
        ;; pop means:
        wet-in-wet (* wet-specialist-relf wet-in-wet-fit)
        wet-in-dry (* wet-specialist-relf wet-in-dry-fit)
        dry-in-dry (* dry-specialist-relf dry-in-dry-fit)
        dry-in-wet (* dry-specialist-relf dry-in-wet-fit)
        wet-env-sum (+ dry-in-wet wet-in-wet)
        dry-env-sum (+ wet-in-dry dry-in-dry)]
    (summary-fn dry-env-relf dry-env-sum wet-env-sum)))

(defn pop-arith-mean
  [dry-env-relf dry-env-sum wet-env-sum]
  (let [wet-env-relf (- 1 dry-env-relf)]
    (+ (* dry-env-sum dry-env-relf)
       (* wet-env-sum wet-env-relf))))

(defn wetdry-pop-arith-mean
  [dry-specialist-relf]
  (wetdry-pop-summary pop-arith-mean dry-specialist-relf))

(defn pop-geom-mean
  [dry-env-relf dry-env-sum wet-env-sum]
  (let [wet-env-relf (- 1 dry-env-relf)]
    (* (m/pow dry-env-sum dry-env-relf)
       (m/pow wet-env-sum wet-env-relf))))

(defn wetdry-pop-geom-mean
  [dry-specialist-relf]
  (wetdry-pop-summary pop-geom-mean dry-specialist-relf))


(comment
  [(wetdry-pop-arith-mean 0.5) (wetdry-pop-arith-mean' 0.5)]
)

;; Wet specialist vs dry specialist--population arithmetic means rel to
;; dry-specialist relf:
(let [step 0.01
      xs (range 0 (+ 1 step) step)
      ys (map wetdry-pop-arith-mean xs)]
  (-> (tc/dataset {:x xs :y ys})
      (plotly/layer-line {:=x :x, :=y, :y})
      plotly/plot
      ;(assoc-in [:data 0 :line :width] 1)
      ))

;; Wet specialist vs dry specialist--population geometric means rel to
;; dry-specialist relf:
(let [step 0.01
      xs (range 0 (+ 1 step) step)
      ys (map wetdry-pop-geom-mean xs)]
  (-> (tc/dataset {:x xs :y ys :x-vert [0.56 0.56] :y-vert [0.76 0.796]})
      (plotly/layer-line {:=x :x, :=y, :y})
      (plotly/layer-line {:=x :x-vert, :=y, :y-vert})
      plotly/plot
      ;(assoc-in [:data 0 :line :width] 1)
      ))


