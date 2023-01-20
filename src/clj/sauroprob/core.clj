(ns sauroprob.core
  (:require [fitdistr.core :as fitc]
            [fitdistr.distributions :as fitd]
            ;[clojure.math.numeric-tower :as m]
            [oz.core :as oz]
            [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]))


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
  "The logistic function with parameter r applied to x.  If x is
  missing, returns the function with parameter r."
  ([r] (partial logistic r))
  ([r x]
   (* r x (- 1 x))))

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
  [label x-min x-max x-increment f]
  (let [x-range (- x-max x-min)
        xs (irange x-min x-max x-increment)
        ys (map f xs)]
    (map (fn [x y] {"x" x, "y" y, "label" label}) xs ys)))

(defn vl-iter-segments
  "Returns a sequence of three Vega-Lite points representing two 
  connected line segments.  The first goes from the line y=x vertically
  to the plot for f.  The second then goes horizontally to y=x."
  [label f x]
  (let [x' (f x)]
    [{"x" x, "y" x, "label" label, "ord" 0}      ; Vega-Lite sorts points from left to right by
     {"x" x, "y" x', "label" label, "ord" 1}     ;  default. Need to order points for lines
     {"x" x', "y" x', "label" label, "ord" 2}])) ;  that go right to left, to avoid bad lines.

(defn vl-iter-line
  "Returns a Vega-Lite line spec containing two line segments which 
  together represent the mapping from x to x'=(f x).  The points will
  be labeled \"mapping\" + label-suffix."
  [f x label-suffix]
  (-> (hc/xform ht/line-chart 
                :DATA (vl-iter-segments (str "mapping" label-suffix) f x)
                :COLOR "label"
                :SIZE 0.7      ; line thickness
                :MSDASH [1 1]) ; dashed [stroke length, space between]
      (assoc-in [:encoding :order :field] "ord"))) ; walk through lines in order not L-R

(defn vl-iter-lines
  "Returns a sequence of Vega-Lite line specs, each containing two line
  segments, which together represent the mapping from x to x'=(f x).
  There will be iters line specs, beginning with the one for (f init-x),
  then (f (f init-x)), and so on.  If distinguish? is present and is
  truthy, each pair of segments will have a different color."
  [f init-x iters & [distinguish?]]
  (let [xs (take iters (iterate f init-x))]
    (map (partial vl-iter-line f)
         xs
         (if distinguish?
           (map #(str " " %) (irange 1 iters))
           (repeat "s"))))) ; the "s" makes "mapping" into "mappings"


(comment

  (def xs4 (logistic-vals 4 0.3))
  ;; I don't think this is likely to be what I want:
  (fitc/fit :ks :logistic (take 10000 xs4))
  fitc/infer
  fitc/bootstrap

  ;; List possible distributions:
  (sort (keys (methods fitd/distribution-data)))

  ;; Plot points generated by iterating a logistic map
  ;; Not all that informative.  Should be done with a histogram.
  ;; CAN BE DONE INSTEAD with vl-data-ify now.
  (def vl-data (map (fn [x y] {"x" x, "y" y, "label" "yow"})
                    (range 10)
                    (map (fn [x] (* x x)) (range 10))))
  (def vl-spec (hc/xform ht/line-chart :DATA vl-data))
  (oz/view! vl-spec)

  (def mu 2.5)

  (def p-mu (/ (- mu 1) mu))

  ((n-comp (logistic 3) 2) 1/2)

  (oz/start-server!)
  ;; Plot an iterated logistic map as a function from x to f(x)
  (def mu 2.999)
  (oz/view! vl-spec)
  (def vl-spec 
    (let [init-x 0.99
          f (logistic mu)]
      (hc/xform ht/layer-chart
                {:LAYER
                 (concat 
                   [(hc/xform ht/line-chart
                              :DATA [{"x" 0, "y" 0, "label" "y=x"} {"x" 1, "y" 1, "label" "y=x"}]
                              :COLOR "label"
                              :SIZE 1.0)
                    (hc/xform ht/line-chart 
                              :DATA (vl-fn-ify (str "μ=" mu ", iter 1")
                                               0.0 1.001 0.001 f)
                              :COLOR "label")
                    (hc/xform ht/line-chart 
                              :DATA (vl-fn-ify (str "μ=" mu ", iter 2")
                                               0.0 1.001 0.001 (n-comp f 2))
                              :COLOR "label")]
                   (vl-iter-lines (n-comp f 1) init-x 80 false))})))
  (oz/view! vl-spec)

) 
