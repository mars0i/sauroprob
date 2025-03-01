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

;; FIXME It's not working.  Maybe it can't.  wtf.  Or is Tableplot messing it up?
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

(def three
  (tc/concat
    (tc/dataset {:x [-4 1], :y [-4 1], :fun :y=x})
    (fn2dataset [-4.0 1.0] :fun :base
                      (partial um/scaled-exp (- m/E)))
    (fn2dataset [-4.0 1.0] :fun :comp2
                      (msc/n-comp (partial um/scaled-exp (- m/E)) 2))
    (fn2dataset [-4.0 1.0] :fun :comp3
                      (msc/n-comp (partial um/scaled-exp (- m/E)) 3))))

(-> three
    (plotly/base {:=height 600 :=width 600})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    equalize-display-units)

(kind/tex "x^2=\\alpha")

(def f2 (kind/tex "\\sum_{i=0}^\\infty \\mu \\frac{f^2}{Q_{32}}"))

f2

(kind/pprint f2)

(meta f2)


(def two
  (let [λ (- (- m/E) 2.0)
        f (partial um/scaled-exp λ)]
    (tc/concat
      (tc/dataset {:x [-7 0.5], :y [-7 0.5], :fun "y=x"})
      (fn2dataset [-7.0 0.5] :fun "f(x)=λe^x" f)
      (fn2dataset [-7.0 0.5] :fun "f^2" (msc/n-comp f 2)))))

(-> two
    ;(plotly/base {:=height 600 :=width 600})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    ;equalize-display-units
    )

(def rickers
  (let [r 2.25
        f (um/normalized-ricker r)]
    (tc/concat
      (tc/dataset {:x [0 2], :y [0 2], :fun "y=x"})
      (fn2dataset [0 3] :fun "f" f)
      (fn2dataset [0 3] :fun "f^2" (msc/n-comp f 2))
      (fn2dataset [0 3] :fun "f^4" (msc/n-comp f 4)))))

(-> rickers
    ;(plotly/base {:=height 600 :=width 600})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    equalize-display-units
    )
