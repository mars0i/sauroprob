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
    [{"x" x, "y" x, "label" label}
     {"x" x, "y" x', "label" label}
     {"x" x', "y" x', "label" label}]))



(comment
  (find-cycle (range 1000))

  (def xs (concat [7] (range 5) (range 5 1 -1)))
  (find-cycle xs)

  (def xs4 (logistic-vals 4 0.5))
  (def xs4 (logistic-vals 4 0.501))
  (def xs4 (logistic-vals 4 0.25))
  (def xs4 (logistic-vals 4 0.75))
  (def xs4 (logistic-vals 4 0.750000001))
  (def xs4 (logistic-vals 4 0.0000001))
  (take 5 xs4)
  (find-cycle (take 1000000 xs4))

  (def xs3 (logistic-vals 3 0.75))
  (def xs3 (logistic-vals 3 2/3))
  (take 20 xs3)
  (find-cycle (take 1000000 xs3))

  (def xs2 (logistic-vals 2 0.1))
  (def xs2 (logistic-vals 2 0.5))
  (def xs2 (logistic-vals 2 0.9))    ; cycles on 0.5
  (def xs2 (logistic-vals 2 0.99))   ; cycles on slightly less than 0.5
  (def xs2 (logistic-vals 2 0.9999)) ; cycles on 0.5
  (def xs2 (logistic-vals 2 0.1))    ; cycles on 0.5
  (def xs2 (logistic-vals 2 0.01))   ; cycles on slightly less than 0.5
  (def xs2 (logistic-vals 2 0.001))  ; cycles on 0.5
  (def xs2 (logistic-vals 2 9/10))   ; DON't DO THIS.
  (def xs2 (logistic-vals 2 1/2))
  (take 3 xs2)
  (take 20 xs2)
  (find-cycle (take 1000000 xs2))

  (def xs4 (logistic-vals 4 0.3))
  ;; I don't think this is likely to be what I want:
  (fitc/fit :ks :logistic (take 10000 xs4))

  fitc/infer
  fitc/bootstrap


  ;; List possible distributions:
  (sort (keys (methods fitd/distribution-data)))

  (oz/start-server!)

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

  (logistic mu 0.45)
  (n-comp (logistic mu) 1 0.45)
  (n-comp (logistic mu) 2 0.45)

  ;; Plot an iterated logistic map as a function from x to f(x)
  (def vl-spec 
    (let [init-x 0.52
          f (logistic mu)
          f2 (n-comp f 2)]
      (hc/xform ht/layer-chart
                {:LAYER
                 [
                  (hc/xform ht/line-chart 
                            :DATA (vl-fn-ify (str "mu=" mu ", iter 1")
                                             0.0 1.001 0.001 f)
                            :COLOR "label")
                  (hc/xform ht/line-chart 
                            :DATA (vl-iter-segments "mapping" f init-x)
                            :COLOR "label")
                  ;; FIXME:
                  (hc/xform ht/line-chart 
                            :DATA (vl-iter-segments "mapping" f (f init-x))
                            :COLOR "label")
                  ;(hc/xform ht/line-chart 
                  ;          :DATA (vl-iter-segments "mapping" f (f2 init-x))
                  ;         :COLOR "label")
                  ;(hc/xform ht/line-chart 
                  ;          :DATA (vl-fn-ify (str "mu=" mu ", iter 2")
                  ;                           0.0 1.001 0.001 f2)
                  ;          :COLOR "label")
                  (hc/xform ht/line-chart
                            :DATA [{"x" 0, "y" 0, "label" "y=x"} {"x" 1, "y" 1, "label" "y=x"}]
                            :COLOR "label")

                  ]})))

  (oz/view! vl-spec)

) 
