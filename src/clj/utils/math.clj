(ns utils.math
  (:require [clojure.math :as m])) ; new in Clojure 1.11 

;; https://stackoverflow.com/a/73829330/1455243
(defn round-to
  "Rounds 'x' to 'places' decimal places"
  [x places]
  (->> x
       bigdec
       (#(.setScale % places java.math.RoundingMode/HALF_UP))
       .doubleValue))

(comment (round-to 32.12545 3) )


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Logistic and related functions
;;
;; There are two kinds of plots of logistic functions supported below.
;;
;; One kind of plot is simply a plot of a function in the normal sense, but
;; you canp lot the original logist function, or n compositions of it with itself.
;;
;; The other kind of plot gives a "path" starting from an initial value
;; through iterations of the function on the value, the result of
;; application of that function, another application, etc. This is one
;; by showing a line from x at y=0 up to f(x), then right or left to y=x
;; [so that the x value there is the same as the old f(x)], and then 
;; up, or down, to the f curve [since that shows how x=f(x) is mapped
;; by f to f(f(x))], and so on.

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

(defn logistic
  "The logistic function with parameter mu applied to x.  If x is
  missing, returns the function with parameter mu."
  ([mu] (partial logistic mu))
  ([mu x]
   (* mu x (- 1 x))))

;; (aka "parabola gadget" in Myrvold's _Beyond Chance and Credence_)
(def logistic-4
  "([x])
   Applies a logistic function with parameter r=4 to x."
  (partial logistic 4))

(defn iter-vals [f param initial]
  "Returns a lazy sequence of values resulting from iterating a 
  function with parameter param, beginning with given initial
  value init."
  (iterate (partial f param) initial))

(defn logistic-vals
  "Returns a lazy sequence of values resulting from iterating a 
  logistic function with parameter r, beginning with given initial
  state."
  [r initial]
  (iter-vals logistic r initial))

(defn real-ricker
  "Function from Ricker 1954 \"Stock and recruitment\"
  (https://en.wikipedia.org/wiki/Ricker_model), or May and Oster 1976
  _American Naturalist_ \"Bifurcations and Dynamic Complexity in Simple
  Ecological Models\", table 1 equation 1. Rather than using N for absolute
  population size, and K for carrying capacity, we use equation (3) on p.
  577 of May and Oster, using x=N/K, or N=Kx."
  ([r] (partial real-ricker r))
  ([r x] (* x (m/exp (* r (- 1 x))))))

(defn ricker
  "Function from Ricker 1954 \"Stock and recruitment\"
  (https://en.wikipedia.org/wiki/Ricker_model), or May and Oster 1976
  _American Naturalist_ \"Bifurcations and Dynamic Complexity in Simple
  Ecological Models\", table 1 equation 1.  If N is a non-zero integer in
  [0,K], result will be a double in the same range."
  ([r K] (partial ricker r K))
  ([r K N] (* N (m/exp (* r (- 1 (/ N K)))))))

(defn ricker-relf
  "Like ricker, but divides result by K so that the result is a double
  representing a non-integer relative frequency (which need not correspond
  to a rational N/K)."
  ([r K] (partial ricker-relf r K))
  ([r K N] (/ (ricker r K N) K)))

(defn floored-ricker
  "Ricker function wrapped in floor so it rounds down to the nearest
  integer as a double."
  ([r K] (partial floored-ricker))
  ([r K N] (m/floor (ricker r K N))))

(defn floored-ricker-relf
  "Like floored-ricker, but divides result by K so that the result is a
  double representing a relative frequency N/K."
  ([r K] (partial floored-ricker-relf r K))
  ([r K N] (/ (floored-ricker r K N) K)))

(comment
  ;; These results are as expected.
  (real-ricker 3.5 0.4)
  (ricker-relf 3.5 1000 400)

  ((ricker-relf 1.5 1000) 400)
  (ricker 1.5 1000 400)
  ((ricker 1.5 1000) 400)
  (floored-ricker 1.5 1000 400)
  (floored-ricker 1.5 10000 4000)
  (floored-ricker-relf 1.5 100 40)
  (floored-ricker-relf 1.5 1000 400)
  (floored-ricker-relf 1.5 10000 4000)
  (floored-ricker-relf 1.5 100000 40000)
  (floored-ricker-relf 1.5 1000000 400000)
)

;; FIXME: width calc isn't working right
(defn tent-fn
  "Returns a tent function on [0,2] with a max value y=height at x=width/2."
  [height width]
  (fn [x]
    (+ height (* height (- (abs (- x (/ width 2.0))))))))
                        
