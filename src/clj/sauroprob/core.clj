(ns sauroprob.core
  (:require [fitdistr.core :as fitc]
            [fitdistr.distributions :as fitd]
            ;[clojure.math.numeric-tower :as m]
            [oz.core :as oz]
            [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]))


(defn logistic
  "The logistic function with parameter r."
  [r x]
  (* r x (- 1 x)))

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

(defn vl-data-fy
  [label x-min x-max ys]
  (let [num-ys (count ys)
        x-range (- x-max x-min)
        x-increment (/ x-range num-ys)
        xs (irange x-min x-max x-increment)]
    (map (fn [x y] {"x" x, "y" y, "label" label}) xs ys)))


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
  (def vl-data (map (fn [x y] {"x" x, "y" y, "label" "yow"})
                    (range 10)
                    (map (fn [x] (* x x)) (range 10))))
  (def vl-spec (hc/xform ht/line-chart :DATA vl-data))
  (oz/view! vl-spec)

  (def vl-spec (hc/xform ht/point-chart
                         :DATA (vl-data-fy "yow" 0.0 1.0 (take 1000 xs4))))
  (oz/view! vl-spec)


) 
