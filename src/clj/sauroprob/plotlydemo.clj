^:kindly/hide-code
(ns sauroprob.plotlydemo
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [tech.v3.datatype.datetime :as datetime]
            [tech.v3.dataset.print :as print]
            [scicloj.kindly.v4.kind :as kind]
            ;[clojure.string :as str]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]
            ;[aerial.hanami.templates :as ht]
           ))


(def data {:x [1 2 2 1 1 1.2]
           :y [1 1 3 3 2 2.5]})

;; How to specify equal units on both axes:
(-> data
    tc/dataset
    (plotly/layer-line {:=x :x, :=y :y})
    plotly/plot ; needed to convert Hanami/Plotly edn to pure Plotly edn
    (assoc-in [:layout :yaxis :scaleanchor] :x)
    (assoc-in [:layout :yaxis :scaleratio] 1)) ; 1 is the default, but this can be used to specify other ratios


;; Data from https://plotly.com/javascript/axes/#fixedratio-axes
(def data2 {:x [0,1,1,0,0,1,1,2,2,3,3,2,2,3]
            :y [0,0,1,1,3,3,2,2,3,3,1,1,0,0]})

(-> data2
    tc/dataset
    (plotly/layer-line {:=x :x
                        :=y :y
                        :=mark-color "blue"
                        })
    plotly/plot
)

;; Examples from Tableplot docs

(-> (rdatasets/datasets-iris)
    (plotly/splom
     {:=colnames [:sepal-length :sepal-width :petal-length :petal-width]
      :=color :species
      :=height 800
      :=width 600}))

;; Why is it not flush with margins?  If you remove the big spots, it's
;; flush on left and right.
(-> (rdatasets/ggplot2-economics_long)
    (tc/select-rows #(-> % :variable (= "unemploy")))
    (plotly/layer-point {:=x :date
                         :=y :value
                         :=mark-color "green"
                         :=mark-size 20
                         :=mark-opacity 0.5})
    (plotly/layer-line {:=x :date
                        :=y :value
                        :=mark-color "purple"}))


;; Example from docs
(-> (rdatasets/datasets-iris)
    (tc/random 10 {:seed 1})
    (plotly/layer-point
     {:=x :sepal-width
      :=y :sepal-length
      :=color :species
      :=size :species
      ;:=mark-size 20
      :=mark-opacity 0.6}))
