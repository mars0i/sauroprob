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

;; [xmin x-max] is the first arg, for use with -> , because it's likely
;; that different fns will share same x coords.
(defn fn2dataset
  [[x-min x-max] catkey catval f & {:keys [steps]}] 
  (let [x-increment (/ (double (- x-max x-min))
                       (or steps plot-steps))
        xs (msc/irange x-min x-max x-increment)
        ys (map f xs)]
    (-> {:x xs, :y ys, catkey catval}
        tc/dataset)))

(defn equalize-display-units
  "Given a Tableplot plot (in either Hanami-key form or full Plotly EDN),
  adds Plotly settings to force the displayed x and y units to be equal."
  [plot]
  (-> plot
      plotly/plot
      (assoc-in [:layout :yaxis :scaleanchor] :x)
      (assoc-in [:layout :yaxis :scaleratio] 1)
      ;; make same grid lines?  not this, doesn't work:
      ;(assoc-in [:layout :grid :xgap] 1)
      ;(assoc-in [:layout :grid :ygap] 1)
      ))

(def all
  (tc/concat
    (-> (fn2dataset [-4.0 1.0] :fun :base
                      (partial um/scaled-exp (- m/E))
                      :keysuffix "1"))
    (-> (fn2dataset [-4.0 1.0] :fun :comp2
                      (msc/n-comp (partial um/scaled-exp (- m/E)) 2)
                      :keysuffix "2"))
    (-> (fn2dataset [-4.0 1.0] :fun :comp3
                      (msc/n-comp (partial um/scaled-exp (- m/E)) 3)
                      :keysuffix "3"))))

(-> all
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    equalize-display-units)


(comment
         ;; old version
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

)
