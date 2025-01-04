;; Hanami and Vega-lite plotting tools for logistic maps, etc.
(ns sauroprob.hanami
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
;; Hanami and Vega-lite plotting tools

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

(defn make-one-fn-vl-spec 
  "Makes a Vega-lite spec for the function f applied to itself num-compositions 
  times, where num-compositions >= 1.
  ADD REST OF DOCSTRING"
  [x-min x-max f param num-compositions]
   (let [paramed-f (f param)]
     (hc/xform ht/line-chart
               :TITLE (str "r=" param)
               :DATA (vl-fn-ify (str "F" (st/u-sup-char num-compositions) " r=" param)
                                x-min x-max 0.001 (msc/n-comp paramed-f num-compositions))
               :COLOR "label")))

(defn make-fn-vl-specs
  "Makes a sequence of Vega-lite specs for the function f, then its composition
  with itself, up to num-compositons.
  ADD REST OF DOCSTRING"
  [x-min x-max f param num-compositions]
  (map (partial make-one-fn-vl-spec x-min x-max f param)
       (msc/irange 1 num-compositions)))


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
    ;(prn fixedpt-x fixedpt-y [x-min y-left] [x-max y-right]) ; DEBUG
    (hc/xform ht/line-chart
              :DATA [{"x" x-min, "y" y-left, "label" "y=-x"} {"x" x-max, "y" y-right, "label" "y=-x"}]
              :COLOR "label"
              :SIZE 0.4)))

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

;; TODO move fixedpt diagonal and escape horizontal lines to separate
;; functions or constants, and allow them to be passed as the & arg,
;; and then concated into the :LAYER arg.  i.e. allow arbitrary single-line
;; additions.
(defn make-vl-spec 
  "ADD DOCSTRING"
  [x-min x-max f param num-compositions init-xs num-iterations & {:keys [fixedpt-x addl-plots]}]
  ;(prn fixedpt-x addl-plots) ; DEBUG
  (let [paramed-f (f param)]
    (hc/xform ht/layer-chart
              :LAYER
               (concat 
                 ; y=x diagonal line that's used in mapping to next value:
                 [(hc/xform ht/line-chart
                            :DATA [{"x" x-min, "y" x-min, "label" "y=x"} {"x" x-max, "y" x-max, "label" "y=x"}]
                            ; FIXME This isn't doinmg what I hoped:
                            ;:XSCALE {"domain" [x-min x-max]}
                            ;:YSCALE {"domain" [x-min x-max]} ; set y display dimensions to the same values
                            :COLOR "label"
                            :SIZE 1.0)]
                 ; Curves (f x), (f (f x)), etc.--num-compositions of them:
                 (make-fn-vl-specs x-min x-max f param num-compositions)
                 ;; Plot lines showing iteration through logistic function starting from init-x:
                 (mapcat (fn [init-x] 
                           (vl-iter-lines-charts (msc/n-comp paramed-f 1) param init-x num-iterations (str "r=" param ", x=" init-x)))
                         init-xs)
                 ;; If extra arg, it's the x coord of the fixed point (x, f x), and indicates we want a faint line with slope -1 through it:
                 (when fixedpt-x [(neg-one-line x-min x-max (f param) fixedpt-x)])
                 addl-plots))))

