;; Hanami and Vega-lite plotting tools for logistic maps, etc.
(ns sauroprob.hanami
  (:require 
            ;[clojure.math.numeric-tower :as m]
            [clojure.math :as m] ; new in Clojure 1.11 
            [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]
            [utils.json :as json]
            [utils.string :as st]
            [utils.misc :as msc]
            [utils.math :as um]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Hanami and Vega-lite plotting tools for exploring population maps

;; Default number of steps to plot a curve; the plot range will usually 
;; be divided into this many steps:
(def plot-steps 200)
;(def x-increment 0.01)

(defn vl-fn-ify
  "Given a function f, returns a sequence of Vega-Lite points with x
  coordinates running from x-min to x-max, inclusive, in steps of size
  x-increment.  The y coordinates are results of applying f to the x
  coordinates. label can be used to identify these as distinct points in
  Vega-Lite.  This can be used to plot f."
  [label x-min x-max x-increment f]
  (let [x-range (- x-max x-min)
        xs (msc/irange x-min x-max x-increment)
        ys (map f xs)]
    (map (fn [x y] {"x" x, "y" y, "label" label}) xs ys)))

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

(defn vl-plot-seq
  "Simple function that generates a Vega-lite plot of the values in ys,
  with each plotted at a subsequent integer x value.  Note: ys should be
  of finite length."
  [label ys]
  (let [pts (map vector 
                 (range (count ys))
                 ys)
        vl-pts (map (fn [[x y]] {"x" x, "y" y, "label" label}) pts)]
      (hc/xform ht/line-chart
                :DATA vl-pts
                :COLOR "label"
                :SIZE 1)))

(comment
  (require '[oz.core :as oz])
  (oz/start-server!)

  (oz/view! (vl-plot-seq "normal" (take 100 (um/iter-vals um/normalized-ricker [3.0] 0.1))))
  (oz/view! (vl-plot-seq "100" (take 100 (iterate (um/normalize um/floored-ricker 100 3.0) 0.1))))
  (oz/view! (vl-plot-seq "1K" (take 100 (iterate (um/normalize um/floored-ricker 1000 3.0) 0.1))))
  (oz/view! (vl-plot-seq "10K" (take 100 (iterate (um/normalize um/floored-ricker 10000 3.0) 0.1))))
  (oz/view! (vl-plot-seq "100K" (take 100 (iterate (um/normalize um/floored-ricker 100000 3.0) 0.1))))

)


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
  [f f-param x label] ; old version: [f f-param x label-suffix]
  (-> (hc/xform ht/line-chart 
                :DATA (vl-iter-segment-pair f f-param x label)  ;: old version: DATA (vl-iter-segment-pair f f-param x (str "mapping" label-suffix))
                :COLOR "label"
                :SIZE 1.5      ; line thickness
                :MSDASH [5 2]) ; dashed [stroke length, space between]
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
          (repeat label))))) ; old version: "s" instead of label

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; All-in-one function(s) to plot logistic maps, etc.

;;   y = -x + b
;; where y0 = f(x0), so
;;   f(x0) = -x0 + b
;; or
;;   b = f(x0) + x0
(defn neg-one-line
  "Given f and fixedpt-x, uses Hanami to construct a faint line 
  with slope -1 through (fixedpt-x, f fixedpt-x), within [x-min, x-max]."
  [x-min x-max f fixedpt-x]
  (let [fixedpt-y (f fixedpt-x)
        linefn (fn [x] (+ (- x) fixedpt-x fixedpt-y))
        y-left (linefn x-min)
        y-right (linefn x-max)]
    (-> (hc/xform ht/line-chart
                  :DATA [{"x" x-min, "y" y-left, "label" "y=-x"} {"x" x-max, "y" y-right, "label" "y=-x"}]
                  :COLOR "label"
                  :SIZE 0.4)
        (assoc-in [:mark :clip] true)))) ; prevent from exceeding y boundaries of plot domain if using :XSCALE, :YSCALE

(comment
  (def yo (sauroprob.hanami/neg-one-line 0.0 1.0 (um/logistic 2.0) 0.5))
)

(defn horiz
  "Vega-line horizontal line from x-min (default 0.0) to x-max (default 1.0)
  at height."
  ([height] (horiz 0.0 1.0 height))
  ([x-min x-max height]
   (hc/xform ht/line-chart
             :DATA [{"x" x-min, "y" height, "label" "escape"} {"x" x-max, "y" height, "label" "escape"}]
             :COLOR "label"
             :SIZE 0.5)))

;; NOTE I'm calling this repeatedly and inefficiently recalculating the same values in each composed function.
(defn make-one-fn-vl-spec-fn
  "Returns a function of num-compositions that makes a Vega-lite spec that
  plots a function f applied params and itself num-compositions times from
  x-min to x-max. num-compositions should be >= 1."
  [x-min x-max f params]
  (fn [num-compositions]
    (let [paramed-f (apply f params)]
      (hc/xform ht/line-chart
                :TITLE (str "params: " params)
                :DATA (vl-fn-ify (str "F" (st/u-sup-char num-compositions) "params: " params)
                                 x-min x-max
                                 (/ (- x-max x-min) (double plot-steps))
                                 (msc/n-comp paramed-f num-compositions))
                :COLOR "label"))))

;; TODO Replace number of compositions with a sequence of composition numbers.
;; TODO? Maybe don't apply f to params, but instead just partial the params into the function.
;;      Well, the advantage of separating out the parameters is that they can be
;;      put into a label on the plot.  This happens when make-one-fn-vl-spec-fn is called.
;; NOTE This inefficiently recalculates the same values in each composed
;; function n order to plot them.
(defn make-vl-spec 
  "Given a function f, applies it to params, and plots num-compositions of
  it from x-min to x-max.  Also plots the map lines from init-xs to the
  function to the diagonal y=x. If `:fixedpt-x x` is present, plots a
  diagonal with slope -1 through (x,x); this can make it easier to see
  whether the slope of f is greater than or less than -1.  The value of
  :addl-plots is a sequence of arbitrary additional vega-lite plots."
  [x-min x-max f params num-compositions init-xs num-iterations & {:keys [fixedpt-x addl-plots]}]
  (let [paramed-f (apply f params)]
    (hc/xform ht/layer-chart
              :LAYER
               (concat 
                 ; y=x diagonal line that's used in mapping to next value along with global parameters of the plot:
                 [(hc/xform ht/line-chart
                                      :DATA [{"x" x-min, "y" x-min, "label" "y=x"} {"x" x-max, "y" x-max, "label" "y=x"}]
                                      ;; Intention of this was to make x distances and y diststances on screen the same.
                                      ;; But V-L has no way to do that, apparently: https://github.com/vega/vega-lite/issues/4367
                                      :XSCALE {"domain" [x-min x-max]}
                                      :YSCALE {"domain" [x-min x-max]} ; set y display dimensions to the same values
                                      :COLOR "label"
                                      :SIZE 1.0)]
                 ; Generate vega-lite specs for curves (f x), (f (f x)), etc., num-compositions of them:
                 (map (make-one-fn-vl-spec-fn x-min x-max f params) (msc/irange 1 num-compositions))
                 ;; Plot lines showing iteration through logistic function starting from init-x:
                 (mapcat (fn [init-x] 
                           (vl-iter-lines-charts (msc/n-comp paramed-f 1) params init-x num-iterations (str "params: " params ", x=" init-x)))
                         init-xs)
                 ;; If extra arg, it's the x coord of the fixed point (x, f x), and indicates we want a faint line with slope -1 through it:
                 (when fixedpt-x [(neg-one-line x-min x-max (apply f params) fixedpt-x)])
                 addl-plots))))

