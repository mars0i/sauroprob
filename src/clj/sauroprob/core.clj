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

  ;(def p-mu (/ (- mu 1) mu))

  (def logistic-data
    (mapcat (fn [m]
              (let [mu (um/round-to m 1)] ; strip float slop created by range
                (vl-fn-ify mu 0.0 1.001 0.01 mu (um/logistic mu))))
            (range 1.0 4.1 0.1))) ; don't use integers--some will mess up subs

  ;; TODO Using vl-iter-lines is too high level--that includes Hanami stuff.
  ;; But I want to do that in the local spec.
  (defn make-mapping-data 
    "Return Vega-Lite data representing a series of mapping \"L\" lines
    for a logistic map.  Start from x value init-x, and returns iters 
    pairs of line segments."
    [init-x iters]
    (mapcat (fn [m]
              (let [mu (um/round-to m 1)] ; strip float slop created by range
                (vl-iter-lines-charts (um/logistic mu) mu init-x iters (str "μ=" mu))))
            (range 1.0 4.1 0.1))) ; don't use integers--some will mess up subs

  (def init-x 0.02)
  (def mapping-data (make-mapping-data init-x 10))  ; TODO see comment at make-mapping-data

  ;; THIS WORKS.
  ;; Proof of concept with slider controlling the mu value of plots.
  ;; Uses pre-generated data for each mu value, and then filters on mu.
  ;; TODO: Make plot axes stable, layer addl plots (mappings, F^2, etc.).
  ;;       add a slider for init-x.
  ;;       add a slider for number of iterations of mappings.
  ;;       add in addl mu's (curr 1.0 thru 3.0).
  (def vl-spec2
    (-> 
      (hc/xform ht/line-chart 
                :DATA logistic-data
                :TRANSFORM [{:filter {:field "label" :equal {:expr "MuSliderVal"}}}] ; :equal "mu_slider_val" doesn't work
                :COLOR "label")
      ;; The "params" key should be at the same level as "data".
      (assoc :params [{:name "MuSliderVal" ; name of slider variable
                       :value 2.5            ; default value
                       :bind {:input "range" ; "range" makes it a slider
                              :min 1.0 :max 4.0 :step 0.1}}]) ; slider config
      ) 
    )
  (oz/view! vl-spec2)

  ;; Example of a completely non-working version (because the slider code
  ;; is not at the top level):
  (def vl-spec2bad
    (-> (hc/xform ht/layer-chart
                  {:LAYER
                   [(-> 
      (hc/xform ht/line-chart 
                :DATA logistic-data
                :TRANSFORM [{:filter {:field "label" :equal {:expr "MuSliderVal"}}}] ; :equal "mu_slider_val" doesn't work
                :COLOR "label")
      ;; The "params" key should be at the same level as "data".
      (assoc :params [{:name "MuSliderVal" ; name of slider variable
                       :value 2.5            ; default value
                       :bind {:input "range" ; "range" makes it a slider
                              :min 1.0 :max 4.0 :step 0.1}}]) ; slider config
      )]})))
  (oz/view! vl-spec2bad)
  (oz/view! vl-spec2)


  (def vl-spec3
    (-> (hc/xform ht/layer-chart
                  {:LAYER
                   ;; Note because the y=x line is unaffected by sliders, etc., it fixes the 
                   ;; dimensions of the axes; otherwise we'd need to fix that by other means.
                   [(hc/xform ht/line-chart
                              :DATA [{"x" 0, "y" 0, "label" "y=x"} {"x" 1, "y" 1, "label" "y=x"}]
                              :COLOR "label"
                              :SIZE 1.0
                              :WIDTH 400
                              :HEIGHT 400)
                    (hc/xform ht/line-chart 
                              :DATA logistic-data
                              :TRANSFORM [{:filter {:field "label" :equal {:expr "MuSliderVal"}}}]
                              :COLOR "label"
                              :WIDTH 400
                              :HEIGHT 400)
                    ]})
        ;; The "params" key has to be at the top level (if there are layers, outside the layers vector)
        (assoc :params [{:name "MuSliderVal" ; name of slider variable
                         :value 2.5            ; default value
                         :bind {:input "range" ; "range" makes it a slider
                                :min 1.0 :max 4.0 :step 0.1}}]) ; slider config
        ))
  (oz/view! vl-spec3)

  (json/edn2json-file "yo.json" vl-spec3)

  (def vl-spec4
    (-> (hc/xform ht/layer-chart
                  {:LAYER
                   ;; Note because the y=x line is unaffected by sliders, etc., it fixes the 
                   ;; dimensions of the axes; otherwise we'd need to fix that by other means.
                   [(hc/xform ht/line-chart
                              :DATA [{"x" 0, "y" 0, "label" "y=x"} {"x" 1, "y" 1, "label" "y=x"}]
                              :COLOR "label"
                              :SIZE 1.0
                              :WIDTH 400
                              :HEIGHT 400)
                    (hc/xform ht/line-chart 
                              :DATA logistic-data
                              :TRANSFORM [{:filter {:field "f-param" :equal {:expr "MuSliderVal"}}}]
                              :COLOR "label"
                              :WIDTH 400
                              :HEIGHT 400)
                    mapping-data ; TODO see comment at make-mapping-data
                    ]})
        ;; The "params" key has to be at the top level (if there are layers, outside the layers vector)
        (assoc :params [{:name "MuSliderVal" ; name of slider variable
                         :value 2.5            ; default value
                         :bind {:input "range" ; "range" makes it a slider
                                :min 1.0 :max 4.0 :step 0.1}}]) ; slider config
        ))
  (oz/view! vl-spec4)



  ;; Plot an iterated logistic map as a function from x to f(x)
  (def mu 4)
  (def init-x 0.2)
  (def num-iterations 7)
  (oz/view! logistic-vl-spec)
  (def logistic-vl-spec
    (let [f (um/logistic mu)]
      (hc/xform ht/layer-chart
                {:LAYER
                 (concat 
                   [(hc/xform ht/line-chart ; y=x diagonal line
                              :DATA [{"x" 0, "y" 0, "label" "y=x"} {"x" 1, "y" 1, "label" "y=x"}]
                              :COLOR "label"
                              :SIZE 1.0)
                    (hc/xform ht/line-chart ; plot logistic function
                              :DATA (vl-fn-ify (str "F" (st/u-sup-char 1) " μ=" mu ", x=" init-x)
                                               0.0 1.001 0.001 init-x f)
                              :COLOR "label")
                    ;(hc/xform ht/line-chart ; plot f^2, logistic of logistic
                    ;          :DATA (vl-fn-ify (str "F" (st/u-sup-char 2) " μ=" mu ", x=" init-x)
                    ;                           0.0 1.001 0.001 init-x (msc/n-comp f 2))
                    ;         :COLOR "label")
                    ;(hc/xform ht/line-chart ; plot f^3
                    ;          :DATA (vl-fn-ify (str "F" (st/u-sup-char 3) " μ=" mu ", x=" init-x)
                    ;                           0.0 1.001 0.001 init-x (msc/n-comp f 3))
                    ;          :COLOR "label")
                    ]
                   ;; plot lines showing iteration through logistic function starting from init-x:
                   (vl-iter-lines-charts (msc/n-comp f 1) mu init-x num-iterations (str "μ=" mu)))})))
  (oz/view! logistic-vl-spec)

(def vl-spec (make-vl-spec um/logistic 4 0.1 5))
(oz/view! vl-spec)
(oz/view! (make-vl-spec um/logistic 2.8 0.99 100))


(def moranspec (make-vl-spec 0.0 2.5 um/moran1950 2.5 0.1 10))
(oz/view! moranspec)
(oz/view! (make-vl-spec 0.0 3.0 um/moran1950 3.0 1.01 10))


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
