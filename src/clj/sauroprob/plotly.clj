^:kindly/hide-code
(ns sauroprob.plotly
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


;; This shows that Plotly lines are written in the order of points, and
;; can go up, backwards, down:
(def data {:x [1 2 2 1 1 1.2]
           :y [1 1 3 3 2 2.5]})

(-> data
    tc/dataset
    ;; I haven't figured out how to set height/width in layer-line, but
    ;; this works for those keys (but not :=margin):
    (plotly/base {;:=height 400
                  ;:=width 400
                  ;:=margins {:t 25 :b 25 :l 25 :r 25}
    })
    (plotly/layer-line {:=x :x
                        :=y :y
                        :=mark-color "purple"
                        })
    ;(assoc-in [:yaxes :scaleanchor] "x")
    ;(assoc-in [:yaxes :scaleratio] 1)
    kind/pprint
    )



;; Where do these go?
(def yo {:=automargin true
         :=margin {:t 25 :b 25 :l 25 :r 25}
         :=height 400
         :=width 400})

;; Examples from docs

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
      :=mark-size 20
      :=mark-opacity 0.6}))
