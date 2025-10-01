;; Exploration of example on p. 192 of Seger and Brockmann,
;; "What is bet-hedging?",
;; pp. 182--211 in <em>Oxford Surveys in Evolutionary Biology</em> v. 4,
;; 1987, eds. Paul H. Harvey and Linda Partridge.

^:kindly/hide-code
(ns segerbrockmann
  (:require [clojure.math :as m]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [fastmath.random :as fr]
            ;[fastmath.stats :as fs]
            [tablecloth.api :as tc]
            ;[clojisr.v1.r :as R]
            ;[criterium.core :as crit]
            [utils.math :as um]
            ;[utils.misc :as msc]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))


;; Not really about chaos
;; From Seger and Brockman 1987 p. 192 on bet hedging

;; Mixture of the two specialist strategies:

;(clojure.repl/dir clojure.math)

(def wet-env-relf 1/2)
(def dry-env-relf (- 1 wet-env-relf))

(def dry-specialist-fits "A_2 in S&B p. 192" {:wet-env 0.6, :dry-env 1})
(def wet-specialist-fits "A_1 in S&B p. 192" {:wet-env 1, :dry-env 0.58})
(def generalist-fits "A_3, jack-of-all-trades in S&B p. 192" {:wet-env 0.785 :dry-env 0.785})

(defn polymorphic-fits
  "A_4, 'the phenotypically polymorphic allele', S&B p. 192.  env should be
  :dry-env or :wet-env.  Returns a fitness value for the organism in that
  environment.  The dry-specialist or wet-specialist phenotypes are chosen
  randomly with probability 0.56 for the dry-specialist phenotype, and 0.44
  for the wet-specialist phenotype."
  [rng env]
  (if "random (0,1] is > 0.56" ; FIXME
    (dry-specialist-fits env)
    (wet-specialist-fits env)))


(defn pop-summary
  "t1-fits is a map of fitnesses for trait t1, with keys :wet-env and
  :dry-env representing fitnesses in the wet and dry envs, respectively.
  Mutatis mutandis for t2-fits. Alternatively, t<n>-fits could be a
  function expects :wet-env or :dry-env as argument, and returns a fitness
  value (perhaps somewhat randomly). summary-fn is a function such as
  arithmetic mean or geometric mean.  t1-relf is the relative frequency of
  trait t1 in a population with two traits present (so the relative
  frequency of t2 is (1 - relf of t1)."
  [t1-fits t2-fits summary-fn t1-relf]
  (let [t2-relf (- 1 t1-relf)
        ;_ (prn "segerbrockmann" ; DEBUG
        ;       "t1wf" (:wet-env t1-fits)
        ;       "t1df" (:dry-env t1-fits)
        ;       "t2wf" (:wet-env t2-fits)
        ;       "t2df" (:dry-env t2-fits))
        ;; pop means:
        t1-in-wet (* t1-relf (t1-fits :wet-env))
        t1-in-dry (* t1-relf (t1-fits :dry-env))
        t2-in-wet (* t2-relf (t2-fits :wet-env))
        t2-in-dry (* t2-relf (t2-fits :dry-env))
        wet-env-sum (+ t1-in-wet t2-in-wet)
        dry-env-sum (+ t2-in-dry t1-in-dry)]
    (summary-fn dry-env-relf dry-env-sum wet-env-sum)))

(defn pop-arith-mean
  [t1-env-relf t1-env-sum t2-env-sum]
  (let [t2-env-relf (- 1 t1-env-relf)]
    (+ (* t1-env-sum t1-env-relf)
       (* t2-env-sum t2-env-relf))))

(defn pop-geom-mean
  [t1-env-relf t1-env-sum t2-env-sum]
  (let [t2-env-relf (- 1 t1-env-relf)]
    (* (m/pow t1-env-sum t1-env-relf)
       (m/pow t2-env-sum t2-env-relf))))

(defn wetdry-pop-arith-mean
  [dry-specialist-relf]
  (pop-summary wet-specialist-fits dry-specialist-fits 
               pop-arith-mean dry-specialist-relf))

(defn wetdry-pop-geom-mean
  [dry-specialist-relf]
  (pop-summary dry-specialist-fits wet-specialist-fits 
               pop-geom-mean dry-specialist-relf))


;; Wet specialist vs dry specialist population arithmetic means rel to
;; dry-specialist relf:
(let [step 0.01
      xs (range 0 (+ 1 step) step)
      ys (map wetdry-pop-arith-mean xs)]
  (-> (tc/dataset {:x xs :y ys})
      (plotly/layer-line {:=x :x, :=y, :y})
      plotly/plot
      ;(assoc-in [:data 0 :line :width] 1)
      ))

;; FIXME This should be a smooth curve with a peak at around 0.56.
;;
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

(let [step 0.01
      xs (range 0 (+ 1 step) step)
      ys (map (partial pop-summary dry-specialist-fits generalist-fits pop-arith-mean)
              xs)]
  (-> (tc/dataset {:x xs :y ys})
      (plotly/layer-line {:=x :x, :=y, :y})
      plotly/plot
      ;(assoc-in [:data 0 :line :width] 1)
      ))

(let [step 0.01
      xs (range 0 (+ 1 step) step)
      ys (map (partial pop-summary dry-specialist-fits generalist-fits pop-geom-mean)
              xs)]
  (-> (tc/dataset {:x xs :y ys 
                   ;:x-vert [0.56 0.56] :y-vert [0.76 0.796]
                  })
      (plotly/layer-line {:=x :x, :=y, :y})
      ;(plotly/layer-line {:=x :x-vert, :=y, :y-vert})
      plotly/plot
      ;(assoc-in [:data 0 :line :width] 1)
      ))

nil
