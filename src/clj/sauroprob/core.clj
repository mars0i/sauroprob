(ns sauroprob.core
  (:require [fitdistr.core :as fitc]
            [fitdistr.distributions :as fitd]
            ;[clojure.math.numeric-tower :as m]
            [clojure.math :as m] ; new in Clojure 1.11 
            [oz.core :as oz]
            [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]
            [utils.json :as json]
            ))


;; Can be used with partial to produce the composition of f with itself,
;; n times. Applying the resulting function is only very slightly slower
;; than running with all three arguments.
(defn n-comp
  "Applies f to x, and then applies f to the result, performing n
  applications of f. Like 'iterate', but without constructing a lazy
  sequence of intermediate values.  With two arguments, uses 'partial'
  to produce a function that performs the same iteration."
  ([f n] (partial n-comp f n))
  ([f n x]
   (loop [i n, y x]
     (if (pos? i)
       (recur (dec i), (f y))
       y))))

(comment 
  ;; Alternate version of n-comp:

  ;; Blows stack beyond about 19K iterations.
  (defn n-comp-bad
    [f n]
    (apply comp (repeat n f)))

  ;; Doesn't blow the stack, but incredibly slow.
  (defn n-comp-slow
    [f n x]
    (last (take n (iterate f x))))
)


(defn logistic
  "The logistic function with parameter mu applied to x.  If x is
  missing, returns the function with parameter r."
  ([mu] (partial logistic mu))
  ([mu x]
   (* mu x (- 1 x))))

(def logistic-4
  "([x])
   Applies a logistic function with parameter r=4 to x."
  (partial logistic 4))

(defn logistic-vals
  "Returns a lazy sequence of values resulting from iterating a 
  logistic function with parameter r, beginning with given initial
  state."
  [r initial]
  (iterate (partial logistic r) initial))

(defn find-cycle
  "Loops through the values in sequence xs, looking for the first value
  that has already appeared in the sequence.  If the value is found, a
  map is returned, otherwise nil if no cycles are found.  The map has
  keys :value for the number that cycles, :period for the period of the
  cycle, and :starts-at for the zero-based index of the value that
  starts the cycle. Note that the test for recurrence uses a clojure
  map, which uses the function 'hash' to determine identity.  This means
  that numbers that appear to be equal but that are of different numeric
  data types may or may not be treated as identical.  The user should
  insure that elements in the sequence are all of the same type."
  [xs]
  (loop [ys xs, i 0, seen {}]  ; seen is "inverted vector": look up by val, returns index
    (if (empty? ys)
      nil
      (let [y (first ys)
            prev-idx (seen y)]
        (if prev-idx 
          {:value y :period (- i prev-idx) :starts-at prev-idx}  ; old version: [y (- i prev-idx) prev-idx]
          (recur (rest ys) (inc i) (assoc seen y i)))))))

;; By John Collins at https://stackoverflow.com/a/68476365/1455243
(defn irange
  "Inclusive range function: end element is included."
  ([start end step]
   (take-while (if (pos? step) #(<= % end) #(>= % end)) (iterate #(+ % step) start)))
  ([start end]
   (irange start end 1))
  ([end]
   (irange 0 end))
  ([] (range)))

(defn vl-data-ify
  "Given a sequence ys of results of a function, returns a sequence
  of Vega-Lite points with ys as y coordinates, and x coordinates
  evenly spaced between x-min and x-max.  label can be used to identify
  these as distinct points in Vega-Lite."
  [label x-min x-max ys]
  (let [num-ys (count ys)
        x-range (- x-max x-min)
        x-increment (/ x-range num-ys)
        xs (irange x-min x-max x-increment)]
    (map (fn [x y] {"x" x, "y" y, "label" label}) xs ys)))

(defn vl-fn-ify
  "Given a function f, returns a sequence of Vega-Lite points with x
  coordinates running from x-min to x-max, inclusive, in steps of size
  x-increment.  The y coordinates are results of applying f to the x
  coordinates. label can be used to identify these as distinct points in
  Vega-Lite."
  [label x-min x-max x-increment f-param f]
  (let [x-range (- x-max x-min)
        xs (irange x-min x-max x-increment)
        ys (map f xs)]
    (map (fn [x y] {"x" x, "y" y, "f-param" f-param, "label" label}) xs ys)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Construct mapping lines as a single ordered VL sequence

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
  (vl-iter-lines  (logistic 2.5) 2.5 0.8 5 "yow")
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
  [f f-param x label-suffix]
  (-> (hc/xform ht/line-chart 
                :DATA (vl-iter-segment-pair f f-param x (str "mapping" label-suffix))
                :COLOR "label"
                :SIZE 1.0      ; line thickness
                :MSDASH [1 1]) ; dashed [stroke length, space between]
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
           (map #(str " " %) (irange 1 iters))
           (repeat "s"))))) ; the "s" makes "mapping" into "mappings"

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn u-sub-char
  "If n is a single-digit integer in [0, ..., 9], returns the
  Unicode supscript character for n.  Otherwise returns nil."
  [n]
  (if (and (integer? n)
           (not (neg? n))
           (>= n 0)
           (<= n 9))
    (char (+ 0x2080 n))
    nil))


;; Unicode supports a limited number of supscript and superscript characters,
;; including single-digit integers.  The superscripts are inconsistent because
;; some are inherited from an earlier character encoding.
(defn u-sup-char
  "If n is a single-digit integer in [0, ..., 9], returns the
  Unicode superscript character for n.  Otherwise returns nil."
  [n]
  (when (integer? n)
    (cond (= n 0) \u2070  ; superscript/subscript block
          (= n 1) \u00B9  ; Latin-1 supplement block
          (= n 2) \u00B2  ; Latin-1 supplement block
          (= n 3) \u00B3  ; Latin-1 supplement block
          (and (>= n 4) (<= n 9)) (char (+ 0x2070 n)) ; superscript/subscript block
          :else nil)))

(comment
  (map #(str "F" (u-sub-char %)) (concat (range 11) [0.2 -3])) ; last 3 s/b empty since nil
  (map #(str "F" (u-sup-char %)) (concat (range 11) [0.2 -3])) ; last 3 s/b empty since nil
)

;;TODO Make string versions of above that take arbitrary positive integers
;; and return strings of superscript or subscript characters.


;; https://stackoverflow.com/a/73829330/1455243
(defn round-to
  "Rounds 'x' to 'places' decimal places"
  [x places]
  (->> x
       bigdec
       (#(.setScale % places java.math.RoundingMode/HALF_UP))
       .doubleValue))

(comment (round-to 32.12545 3) )

(comment
  (oz/start-server!)

  (def xs4 (logistic-vals 4 0.3))
  ;; I don't think this is likely to be what I want:
  (fitc/fit :ks :logistic (take 10000 xs4))
  fitc/infer
  fitc/bootstrap

  ;; List possible distributions:
  (sort (keys (methods fitd/distribution-data)))

  (def p-mu (/ (- mu 1) mu))

  (def logistic-data
    (mapcat (fn [m]
              (let [mu (round-to m 1)] ; strip float slop created by range
                (vl-fn-ify mu 0.0 1.001 0.01 mu (logistic mu))))
            (range 1.0 4.1 0.1))) ; don't use integers--some will mess up subs

  ;; TODO Using vl-iter-lines is too high level--that includes Hanami stuff.
  ;; But I want to do that in the local spec.
  (defn make-mapping-data 
    "Return Vega-Lite data representing a series of mapping \"L\" lines
    for a logistic map.  Start from x value init-x, and returns iters 
    pairs of line segments."
    [init-x iters]
    (mapcat (fn [m]
              (let [mu (round-to m 1)] ; strip float slop created by range
                (vl-iter-lines-charts (logistic mu) mu init-x iters (str "μ=" mu))))
            (range 1.0 4.1 0.1))) ; don't use integers--some will mess up subs

  (def init-x 0.99)
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
  (def mu 2.5)
  (def init-x 0.99)
  (oz/view! vl-spec)
  (def vl-spec 
    (let [f (logistic mu)]
      (hc/xform ht/layer-chart
                {:LAYER
                 (concat 
                   [(hc/xform ht/line-chart
                              :DATA [{"x" 0, "y" 0, "label" "y=x"} {"x" 1, "y" 1, "label" "y=x"}]
                              :COLOR "label"
                              :SIZE 1.0)
                    (hc/xform ht/line-chart 
                              :DATA (vl-fn-ify (str "F" (u-sup-char 1) " μ=" mu ", x=" init-x)
                                               0.0 1.001 0.001 f)
                              :COLOR "label")
                    (hc/xform ht/line-chart 
                              :DATA (vl-fn-ify (str "F" (u-sup-char 2) " μ=" mu ", x=" init-x)
                                               0.0 1.001 0.001 (n-comp f 2))
                              :COLOR "label")
                    ;(hc/xform ht/line-chart 
                    ;          :DATA (vl-fn-ify (str "F" (u-sup-char 3) " μ=" mu ", x=" init-x)
                    ;                           0.0 1.001 0.001 (n-comp f 3))
                    ;          :COLOR "label")
                    ]
                   (vl-iter-lines-charts (n-comp f 1) mu init-x 50 (str "μ=" mu)))})))
  (oz/view! vl-spec)



) 
