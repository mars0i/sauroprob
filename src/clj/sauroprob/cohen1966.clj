;; Functions from 
;; @Article{Cohen:OptimizingReprodRandomEnvs,
;;   title =	{Optimizing reproduction in a randomly varying environment},
;;   author =	{Dan Cohen},
;;   journal =	{Journal of Theoretical Biology},
;;   volume =	{12},
;;   number =	{1},
;;   pages =	{119-129},
;;   year =	{1966},
;;   issn =	{0022-5193},
;;   doi =		{https://doi.org/10.1016/0022-5193(66)90188-3},
;; }
(ns sauuroprob.cohen1966
  (:require ;[scicloj.tableplot.v1.plotly :as plotly]
            ;[scicloj.kindly.v4.kind :as kind]
            ;[scicloj.kindly.v4.api :as kindly]
            ;[utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]))

;; Following Clojure tradition, I use lowercase variable names for
;; corresponding uppercase variables in Cohen.

(defn eq9internal
  "The equation that's summed over in equation (9) on page 124. p and y are
  last so that we can partial in d and g and then map over ps and ys."
  [d g p y]
  (* p (/ (+ y d -1)
          (+ (* (- 1 g) (- 1 d)) (* g y)))))

;; Put g last so we can partial-in the other args:
(defn eq9
  "Equation (9) on page 124, which is the partial derivative with respect
  to g of equation (4) on page 120. d is a scalar representing the fraction
  of seeds that decays in each season.  g is the fraction that germinate.
  The values in ps are the probabilities of environmental states.  The
  values in ys are the corresponding number of seeds produced in each
  environmental state.  Note ps and ys should have the same lengths, and
  the elements of ps should sum to 1. This function will return nil if
  either condition is not met.  (Beware of float slop for the test on the
  contents of ps.)"
  [d ps ys g]
  (when (and (= (count ps) (count ys))
             (= 1.0 (reduce + ps)))
    (reduce + (map (partial eq9internal d g)
                   ps ys))))

(comment
  (eq9 0.1
       [0.2 0.5 0.3]
       [10 0.1 4]
       0.5)
)
