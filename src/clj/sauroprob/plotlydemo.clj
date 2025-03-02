^:kindly/hide-code
(ns sauroprob.plotlydemo
  (:require [clojure.math :as m]
            [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]
            [utils.math :as um]
            [utils.misc :as msc]
            [sauroprob.plotly :as spl]))

;; ## My dynamical system examples

(def rickers
  (let [r 2.7
        f (um/normalized-ricker r)]
    (tc/concat
      (tc/dataset {:x [0 2.1], :y [0 2.1], :fun "y=x"})
      (spl/iter-lines 0.75 8 :fun "iter" f)
      (spl/fn2dataset [0 3] :fun "f" f)
      (spl/fn2dataset [0 3] :fun "f^2" (msc/n-comp f 2))
      (spl/fn2dataset [0 3] :fun "f^4" (msc/n-comp f 4)))))

;; It would be simpler to embed the HTML in the vals, but this illustrates
;; the option of adding them in the final stage.
(-> rickers
    (plotly/base {:=height 420 :=width 700})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    (plotly/plot)
    (assoc-in [:data 0 :line :dash] "dash") ; https://plotly.com/javascript/reference/scatter/#scatter-line-dash 
    (assoc-in [:data 0 :name] "<em>y=x</em>") ; https://plotly.com/javascript/reference/scatter/#scatter-name
    (assoc-in [:data 1 :line :dash] "dot")
    (assoc-in [:data 2 :line :width] 3) ; default is 2.  https://plotly.com/javascript/reference/scatter/#scatter-line-width
    (assoc-in [:data 2 :name] "<em>f(x)=xe<sup>r(1-x)</sup></em>") ; 1 shouldn't really be italicized
    (assoc-in [:data 3 :name] "<em>f<sup>2</sup></em>")
    (assoc-in [:data 4 :name] "<em>f<sup>4</sup></em>")
    (spl/equalize-display-units) ; If display dimensions don't fit data, extra space in plot
    ;(kind/pprint)
   )

(def three
  (tc/concat
    (tc/dataset {:x [-4 1], :y [-4 1], :fun :y=x})
    (spl/fn2dataset [-4.0 1.0] :fun :base (partial um/scaled-exp (- m/E)))
    (spl/fn2dataset [-4.0 1.0] :fun :comp2 (msc/n-comp (partial um/scaled-exp (- m/E)) 2))
    (spl/fn2dataset [-4.0 1.0] :fun :comp3 (msc/n-comp (partial um/scaled-exp (- m/E)) 3))))

(-> three
    (plotly/base {:=height 600 :=width 600})
    (plotly/layer-point {:=x :x, :=y, :y, :=color :fun, :=size :fun, :=mark-opacity 0.2,
                        :=name "Yow"})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    spl/equalize-display-units
    (plotly/plot)
    ;(kind/pprint)
    )


(def two
  (let [λ1 (- (- m/E) 2.0)
        f1 (partial um/scaled-exp λ1)
        λ2 (- (- m/E) 3.0)
        f2 (partial um/scaled-exp λ2)]
    (tc/concat
      (tc/dataset {:x [-7 0.5], :y [-7 0.5], :fun "y=x"})
      (spl/fn2dataset [-7.0 0.5] :fun "f<sub>1</sub>(x)=λ<sub>1</sub>e<sup>x</sup>" f1)
      (spl/fn2dataset [-7.0 0.5] :fun "f<sub>1</sub><sub>2</sub>" (msc/n-comp f1 2))
      (spl/fn2dataset [-7.0 0.5] :fun "f<sub>2</sub>(x)=λ<sub>1</sub>e<sup>x</sup>" f2)
      (spl/fn2dataset [-7.0 0.5] :fun "f<sub>2</sub><sub>2</sub>" (msc/n-comp f2 2))
    )))

(-> two
    (plotly/base {:=height 600 :=width 600})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    (spl/equalize-display-units)
    )

(let [λ (- m/E)
      f (partial um/scaled-exp λ)]
  (-> (tc/concat
        (tc/dataset {:x [-7 0.5], :y [-7 0.5], :fun "<em>y</em>=<em>x</em>"})
        (spl/fn2dataset [-7.0 0.5] :fun "f(x)=λe<sup>x</sup>" f))
      (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
      (spl/equalize-display-units)))


;; This illustrates a method for replacing values at the last step in order
;; to change how they are displayed.  Using an array index seems fragile.
(let [λ (- m/E)
      f (partial um/scaled-exp λ)]
  (-> (tc/concat
        (tc/dataset {:x [-7 0.5], :y [-7 0.5], :fun "y=x"})
        (spl/fn2dataset [-7.0 0.5] :fun "scaled-exp" f))
      (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
      (plotly/plot)
      ;; This works but it's fragile, and only replaces one value:
      (assoc-in [:data 1 :name] "f(x)=λe<sup>x</sup>")
      ;; This works but it's too complicated (cleaner with pre-defined fns):
      ;(update :data (fn [v] (mapv
      ;                        (fn [m] (update m :name #(if (= % "scaled-exp") "f(x)=λe<sup>x</sup>" %)))
      ;                        v)))                    ;; use `cond` for more replacements
      ;(kind/pprint)
      ))


;; ## Simple examples to explore units and margins

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

;; ## Examples based on Tableplot docs

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


(-> (rdatasets/datasets-iris)
    (tc/random 10 {:seed 1})
    (plotly/layer-point
     {:=x :sepal-width
      :=y :sepal-length
      :=color :species
      :=size :species
      ;:=mark-size 20
      :=mark-opacity 0.6}))
