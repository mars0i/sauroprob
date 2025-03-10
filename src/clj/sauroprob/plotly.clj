^:kindly/hide-code
(ns sauroprob.plotly
  (:require [clojure.math :as m] ; new in Clojure 1.11 
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            ;[scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.string :as st]
            [utils.math :as um]
           ))


(def plot-steps 400)

;; [xmin x-max] is the first arg, for use with -> , because it's likely
;; that different fns will share same x coords.
(defn fn2dataset
  "Generates a TMD dataset of points in which the coordinates are (x, f(x))
  from x-min to x-max, with an additional field named catkey, with a single
  value catval.  This key/val pair can be used to identify these coordinates
  after they are merged into another dataset."
  [[x-min x-max] catkey catval f & {:keys [steps]}] 
  (let [x-increment (/ (double (- x-max x-min))
                       (or steps plot-steps))
        xs (msc/irange x-min x-max x-increment)
        ys (map f xs)]
    (-> {:x xs, :y ys, catkey catval}
        tc/dataset)))

(defn equalize-display-units
  "Given a Tableplot plot (in either Hanami-key form or full Plotly EDN),
  adds Plotly settings to force the displayed x and y units to be equal.
  (Note if the plot's display dimensions are too large in one dimension,
  there will be extra space in the plot outside of the plotted data.)"
  [plot]
  (-> plot
      plotly/plot
      (assoc-in [:layout :yaxis :scaleanchor] :x)
      (assoc-in [:layout :yaxis :scaleratio] 1)))

;(defn next-iter-seg
;  [f x]
;  {:x [x x]
;   :y [x (f x)]})

(defn next-vert-seg
  "ADD DOCSTRING"
  [[x next-x]]
  {:x [x x]
   :y [x next-x]})

;; We only construct vertical segments explicitly; by chaining
;; these together, the lines that connect them are the horizontal
;; segments.
(defn iter-lines
  "ADD DOCSTRING"
  [init-x iters catkey catval f]
  (->> (iterate f init-x)
       (take iters)
       (partition 2 1)
       (map next-vert-seg)
       (apply merge-with into)
       (#(assoc % catkey catval)) ; since threading last
       tc/dataset))

;(defn plot-seq
;  "Simple function that generates a Plotly plot of the values in ys,
;  with each plotted at a subsequent integer x value.  ys should be
;  of finite length."
;  [ys]

(comment
  (iter-lines1 0.75 5 :fun "ya" (um/normalized-ricker 2.7))
  (iter-lines 0.75 5 :fun "ya" (um/normalized-ricker 2.7))
)
