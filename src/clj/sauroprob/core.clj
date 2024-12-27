(ns sauroprob.core
  (:require 
            ;[clojure.math.numeric-tower :as m]
            [clojure.math :as m] ; new in Clojure 1.11 
            [oz.core :as oz]
            [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]
            [utils.json :as json]
            [utils.string :as st]
            [utils.misc :as msc]
            [utils.math :as um]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Hanami/Vega-lite plotting tools

(defn vl-data-ify
  "Given a sequence ys of results of a function, returns a sequence
  of Vega-Lite points with ys as y coordinates, and x coordinates
  evenly spaced between x-min and x-max.  label can be used to identify
  these as distinct points in Vega-Lite."
  [label x-min x-max ys]
  (let [num-ys (count ys)
        x-range (- x-max x-min)
        x-increment (/ x-range num-ys)
        xs (msc/irange x-min x-max x-increment)]
    (map (fn [x y] {"x" x, "y" y, "label" label}) xs ys)))

(defn vl-fn-ify
  "Given a function f, returns a sequence of Vega-Lite points with x
  coordinates running from x-min to x-max, inclusive, in steps of size
  x-increment.  The y coordinates are results of applying f to the x
  coordinates. label can be used to identify these as distinct points in
  Vega-Lite.  This can be used to plot f."
  [label x-min x-max x-increment f-param f]
  (let [x-range (- x-max x-min)
        xs (msc/irange x-min x-max x-increment)
        ys (map f xs)]
    (map (fn [x y] {"x" x, "y" y, "f-param" f-param, "label" label}) xs ys)))

;; For more info, see discussion at:
;; https://clojurians.zulipchat.com/#narrow/stream/210075-saite-dev/topic/concat.20template/near/279290717
(def grid-chart
  "Template for Vega-Lite \"concat\": multi-view charts.  Lays out
  a series of plots in rows and columns from left to right and top 
  to bottom.  The value of :COLUMNS specifies the number of columns."
  (-> ht/view-base
      (dissoc :encoding)
      (assoc :concat :CONCAT 
             :columns :COLUMNS 
             :resolve :RESOLVE
             :config ht/default-config)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Construct mapping lines as chart elements

(defn vl-iter-segment-pair
  "Returns a sequence of three Vega-Lite points representing two 
  connected line segments.  The first goes from the line y=x vertically
  to the plot for f.  The second then goes horizontally to y=x."
  [f f-param x label]
  (let [x' (f x)]
    [{"x" x, "y" x,   "f-param" f-param, "label" label, "ord" 0}   ; Vega-Lite sorts points from left to right by
     {"x" x, "y" x',  "f-param" f-param, "label" label, "ord" 1}   ;  default. Need to order points for lines
     {"x" x', "y" x', "f-param" f-param, "label" label, "ord" 2}])) ;  that go right to left, to avoid bad lines.

(defn vl-iter-line-chart
  "Returns a Vega-Lite line spec containing two line segments which 
  together represent the mapping from x to x'=(f x).  The points will
  be labeled \"mapping\" + label-suffix."
  [f f-param x label-suffix]
  (-> (hc/xform ht/line-chart 
                :DATA (vl-iter-segment-pair f f-param x (str "mapping" label-suffix))
                :COLOR "label"
                :SIZE 1.25      ; line thickness
                :MSDASH [5 3]) ; dashed [stroke length, space between]
      (assoc-in [:encoding :order :field] "ord"))) ; walk through lines in order not L-R

(defn vl-iter-lines-charts
  "Returns a sequence of Vega-Lite line specs, each containing two line
  segments, which together represent the mapping from x to x'=(f x).
  There will be iters line specs, beginning with the one for (f init-x),
  then (f (f init-x)), and so on.  If distinguish? is present and is
  truthy, each pair of segments will have a different color."
  ([f f-param init-x iters]
   (vl-iter-lines-charts f-param f init-x iters "mapping"))
  ([f f-param init-x iters label]
   (let [xs (take iters (iterate f init-x))]
     (map (partial vl-iter-line-chart f f-param)
          xs
          (repeat "s")))))

(defn vl-iter-lines-charts*
  "Returns a sequence of Vega-Lite line specs, each containing two line
  segments, which together represent the mapping from x to x'=(f x).
  There will be iters line specs, beginning with the one for (f init-x),
  then (f (f init-x)), and so on.  If distinguish? is present and is
  truthy, each pair of segments will have a different color."
  [f init-x iters & [distinguish?]]
  (let [xs (take iters (iterate f init-x))]
    (map (partial vl-iter-line-chart f)
         xs
         (if distinguish?
           (map #(str " " %) (msc/irange 1 iters))
           (repeat "s"))))) ; the "s" makes "mapping" into "mappings"

;; TODO Separate out the function plot, add param to specify whether
;; and how many f^n to plot after plotting f.
(defn make-vl-spec 
  "ADD DOCSTRING"
  ([f param init-x num-iterations] (make-vl-spec 0.0 1.001 f param init-x num-iterations))
  ([x-min x-max f param init-x num-iterations]
  (let [paramed-f (f param)]
    (hc/xform ht/layer-chart
              {:LAYER
               (concat 
                 [(hc/xform ht/line-chart ; y=x diagonal line
                            :DATA [{"x" x-min, "y" x-min, "label" "y=x"} {"x" x-max, "y" x-max, "label" "y=x"}]
                            :COLOR "label"
                            :SIZE 1.0)
                  (hc/xform ht/line-chart ; plot the function
                            :DATA (vl-fn-ify (str "F" (st/u-sup-char 1) " r=" param ", x=" init-x)
                                             x-min x-max 0.001 init-x paramed-f)
                            :COLOR "label")
                  ;(hc/xform ht/line-chart ; plot f^2, logistic of logistic
                  ;          :DATA (vl-fn-ify (str "F" (st/u-sup-char 2) " r=" param ", x=" init-x)
                  ;                           x-min x-max 0.001 init-x (msc/n-comp paramed-f 2))
                  ;         :COLOR "label")
                  ;(hc/xform ht/line-chart ; plot f^3
                  ;          :DATA (vl-fn-ify (str "F" (st/u-sup-char 3) " r=" param ", x=" init-x)
                  ;                           x-min x-max 0.001 init-x (msc/n-comp paramed-f 3))
                  ;          :COLOR "label")
                  ]
                 ;; plot lines showing iteration through logistic function starting from init-x:
                 (vl-iter-lines-charts (msc/n-comp paramed-f 1) param init-x num-iterations (str "r=" param)))}))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(comment
  (oz/start-server!)
(def vl-spec (make-vl-spec um/logistic 4 0.1 5))
(oz/view! vl-spec)
(oz/view! (make-vl-spec um/logistic 2.8 0.99 100))

(def moranspec (make-vl-spec 0.0 2.5 um/moran1950 2.5 0.1 10))
(oz/view! moranspec)

;; two moran1950 plots
(def two-specs [(make-vl-spec 0.0 3.0 um/moran1950 2.0 0.01 9)
                (make-vl-spec 0.0 3.0 um/moran1950 3.0 0.01 9)])
(def grid-spec (hc/xform grid-chart :COLUMNS 2 :CONCAT two-specs))
(oz/view! grid-spec)

;; Four moran1959 plots
(def four-specs [(make-vl-spec 0.0 3.0 um/moran1950 1.5 0.01 10)
                 (make-vl-spec 0.0 3.0 um/moran1950 2.0 0.01 10)
                 (make-vl-spec 0.0 3.0 um/moran1950 2.5 0.01 10)
                 (make-vl-spec 0.0 3.0 um/moran1950 3.0 0.01 10)])
(def grid-spec (hc/xform grid-chart :COLUMNS 2 :ROWS 2 :CONCAT four-specs))
(oz/view! grid-spec)

;; Four logistic plots
(def four-specs [(make-vl-spec 0.0 1.0 um/logistic 2.0 0.01 10)
                 (make-vl-spec 0.0 1.0 um/logistic 3.0 0.01 10)
                 (make-vl-spec 0.0 1.0 um/logistic 4.0 0.01 10)
                 (make-vl-spec 0.0 1.2 um/logistic 4.5 0.01 10)]) ; note different x-max
(def grid-spec (hc/xform grid-chart :COLUMNS 2 :ROWS 2 :CONCAT four-specs))
(oz/view! grid-spec)

)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; CRUFT

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Construct mapping lines as a single ordered VL sequence

(comment

(defn vl-iter-lines
  [f f-param init-x iters label]
  (loop [n iters
         x init-x
         order 0
         segments [{"x" x, "y" x, "f-param" f-param, "label" label, "ord" 0}]]
    (if (zero? n)
      segments
      (let [next-x (f x)
            pt2 {"x" x,      "y" next-x, "f-param" f-param, "label" label, "ord" (+ order 1)}
            pt3 {"x" next-x, "y" next-x, "f-param" f-param, "label" label, "ord" (+ order 2)}]
        (recur (dec n)
               next-x
               (+ order 2)
               (cons pt3 (cons pt2 segments)))))))

(comment
  (vl-iter-lines  (um/logistic 2.5) 2.5 0.8 5 "yow")
)
)


(comment
  (require '[fitdistr.core :as fitc])
  (require '[fitdistr.distributions :as fitd])
  (def xs4 (um/logistic-vals 4 0.3))
  ;; I don't think this is likely to be what I want:
  (fitc/fit :ks :logistic (take 10000 xs4))
  fitc/infer
  fitc/bootstrap

  ;; List possible distributions:
  (sort (keys (methods fitd/distribution-data)))
  (require '[fitdistr.core :as fitc])
  (require '[fitdistr.distributions :as fitd])
  (def xs4 (um/logistic-vals 4 0.3))
  ;; I don't think this is likely to be what I want:
  (fitc/fit :ks :logistic (take 10000 xs4))
  fitc/infer
  fitc/bootstrap

  ;; List possible distributions:
  (sort (keys (methods fitd/distribution-data)))
)
