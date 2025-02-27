^:kindly/hide-code
(ns sauroprob.plotly
  (:require [clojure.math :as m] ; new in Clojure 1.11 
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            ;[scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
           ))


(def plot-steps 400)

(defn fn-dataset
  [[x-min x-max] [y-min y-max] f & {:keys [steps keysuffix]}] 
  (let [x-increment (/ (- x-max x-min) (double (or steps plot-steps)))
        xs (msc/irange x-min x-max x-increment)
        ys (map f xs)
        xkey (keyword (str "x" (or keysuffix "")))
        ykey (keyword (str "y" (or keysuffix "")))]
    (-> {xkey xs, ykey ys}
        tc/dataset)))

(-> (fn-dataset [-4.0 1.0] [-4.0 1.0] (partial um/scaled-exp (- m/E)))
    (plotly/layer-line {:=x :x, :=y, :y}))

(-> (fn-dataset [-4.0 1.0] [-4.0 1.0]
                  (msc/n-comp (partial um/scaled-exp (- m/E)) 2))
    (plotly/layer-line {:=x :x, :=y, :y}))

(def all
  (tc/concat
    (-> (fn-dataset [-4.0 1.0] [-4.0 1.0]
                      (partial um/scaled-exp (- m/E))
                      :keysuffix "1"))
    (-> (fn-dataset [-4.0 1.0] [-4.0 1.0]
                      (msc/n-comp (partial um/scaled-exp (- m/E)) 2)
                      :keysuffix "2"))
    (-> (fn-dataset [-4.0 1.0] [-4.0 1.0]
                      (msc/n-comp (partial um/scaled-exp (- m/E)) 3)
                      :keysuffix "3"))))

(-> all
    (plotly/layer-line {:=x :x1, :=y, :y1})
    (plotly/layer-line {:=x :x2, :=y, :y2})
    (plotly/layer-line {:=x :x3, :=y, :y3}))
