(ns utils.math
  (:require [clojure.math :as m])) ; new in Clojure 1.11 

;; Note that to apply these, f should be a single-arg fn, where the
;; arg is a current pop size.  i.e. the parameters baked should be already
;; backed in e.g. by partial.
;; SEE COMMENT BLOCK BELOW for illustrations/tests using ricker.
(defn rounded [f] (comp m/round f))
(defn floored [f] (comp m/floor f))
(defn ceiled  [f] (comp m/ceil f))

;; https://stackoverflow.com/a/73829330/1455243
;; SHOULD I USE with-precision INSTEAD?
(defn round-to
  "Rounds 'x' to 'places' decimal places"
  [x places]
  (->> x
       bigdec
       (#(.setScale % places java.math.RoundingMode/HALF_UP))
       .doubleValue))

(comment (round-to 32.12545 3) )

;; Is there a more efficient way to do this?
(defn to-ratl-mapper
  "Given a number e.g. between 0 and 1, round it to the nearest rational
  number with K as denominator, and returns that number as a double. (Wrap
  in rationalize if you prefer a ratio.)"
  [to-int-fn]
  (fn [K x] (-> x
                (* K)     ; expand x on [0,1] scale to the K scale
                to-int-fn   ; round to the nearest K-scale long
                ((fn [n] (prn x n K (/ n K)) n)) ; DEBUG
                ; double   ; Not needed with floor, ceil, rint. Added to to-int-fn if necessary.
                (/ K))))   ; reduce the double back to x's original scale

(def round-to-ratl (to-ratl-mapper m/rint))
(def floor-to-ratl (to-ratl-mapper m/floor))
(def ceil-to-ratl (to-ratl-mapper m/ceil))

(defn rounded-to-ratl 
  "Given a function f of one argument, returns a function that performs the
  same computation, but then rounds the return value to the nearest
  rational with denominator K, as a double."
  [K f]
  (comp (partial round-to-ratl K) f))

(defn floored-to-ratl 
  "Given a function f of one argument, returns a function that performs the
  same computation, but then floors the return value to the nearest
  rational with denominator K, as a double."
  [K f]
  (comp (partial floor-to-ratl K) f))

(defn ceiled-to-ratl 
  "Given a function f of one argument, returns a function that performs the
  same computation, but then ceils the return value to the nearest
  rational with denominator K, as a double."
  [K f]
  (comp (partial ceil-to-ratl K) f))

(comment
  (let [K 171789435
        x 0.257318654971]
    [;(obsolete-round-to-ratl K x)
     (round-to-ratl K x)
     ((rounded-to-ratl K identity) x)
     (floor-to-ratl K x)
     ((floored-to-ratl K identity) x)
     (ceil-to-ratl K x)
     ((ceiled-to-ratl K identity) x)])
  
  ;; See below for examples wrapping Ricker functions.
)

(defn iter-vals
  "Returns a lazy sequence of values resulting from iterating a 
  function with parameter param, beginning with 0given initial
  value init."
 [f params initial]
  (iterate (apply f params) initial))

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

;; For tests and examples, see comment block after normalized-ricker
(defn normalize
  "Wraps a function f of a \"carrying capacity\" K, returning a function
  from x in [0,1] to a result (typically) in [0,1], more or less
  representing a relative frequency relative to K.  (It's the job of the
  user to make sure that f and K are appropriately related.)"
  [f K]
  (fn [x]  ; x should be in [0,1]
    (let [n (* x K) ; multiply carrying capacity K by x to get (possibly non-integer) pop size n
          ;; NOTE If inputs should be restricted to integers, this is where that could happen.
          ;; i.e. the function f could floor/round at this point.
          ;; BUT if the input x was a rational over denominator K, it's not needed.
          result (double (f n))] ; result is a new pop size on the K scale
      (/ result K)))) ; so we divide by K
      ;; This will be a double representing a rational with K as
      ;; denominator if f restricted output to integers.
      ;; so that'S what will go into the next iteration.


;; old version
(defn normalize-with-params
  "Wraps a function f of a \"carrying capacity\" K and additional
  parameters, and a population size N, returning instead a function from
  x in [0,1] to a result (typically) in [0,1], more or less representing a
  relative frequency of N relative to K."
  [f K & params]
  (fn [x]
    (let [N (* x K) ; multiply carrying capacity K by x to get pop size N
          result (double ((apply f K params) N))] ; result is a new pop size on the K scale
      (/ result K)))) ; so we divide by K

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Logistic maps

;; The strategy below of defining variants of functions that take
;; fewer arguments and return a function defined by partial or apply
;; duplicates the functionality that's built in in a language like Haskell or Lean.
;; It could be avoided using partial explicitly, but that's verbose.
;; otoh this makes definitions verbose, and more confusing.

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

(defn logistic-contin-deriv
  "The derivative that defines an original continuous logistic function
  (cf. Wimsatt 1980 p. 298)."
  ([K r] (partial logistic-contin-deriv K r))
  ([K r N] (* r N (- 1 (/ N K)))))

(defn williams-bossert-deriv
  "The derivative that defines an a modified continuous logistic function,
  introduced by Wilson and Bossert (cf. Wimsatt 1980 p. 299)."
  ([K C r] (partial williams-bossert-deriv K r))
  ([K C r N] (* r N
                (- 1 (/ N K))
                (- 1 (/ C N)))))

(defn logistic-plus
  "Like a K-scaled discrete logistic function, but adds N to the result of
  the logistic function, i.e. N' = N + rN(1 - N/K). See May 1973 book,
  May's 1973 paper, equation 2 in table 1 in May and Oster, etc. [This is
  another natural extension of the continuous logistic: subtract N from
  both sides let N' approach N.]"
  ([K r] (partial logistic-plus K r))
  ([K r N]
   (* N (+ 1 (* r (- 1 (/ N K)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; "Ricker" functions
;; See docstrings for explanation.

;; May and Oster 1976, p. 594, table 2 say that 2.6924 is an "exact value" for
;; this, but it's clearly just the 4-digit approximation, and using it in
;; experiments shows that you don't easily get chaos.  Or maybe it takes
;; many iterations to get there.  Or maybe it's just a bit of chaos.
(def ricker-chaos-min 
  "Minimum parameter value for r at which a canonical Ricker function (e.g.
  `ricker` or `normalized-ricker`) becomes chaotic."
  2.6924) ; "exact value" of r_c, May and Oster 1976, p. 594, table 2

(defn pre-ricker
  "Equation (7) from Ricker 1954 \"Stock and recruitment\", p. 611. Here w
  is the ratio between two log survival rates s2 in situation 2 and s1 in
  situation 1."
  ([s1] (partial pre-ricker s1))
  ([s1 w] (* w (m/pow s1 (- w 1)))))

(defn original-ricker
  "Equation (10) from Ricker 1954 \"Stock and recruitment\", p. 611.
  Here w is the ratio between two predator abundances p2 in situation
  2 and p1 in situation 1."
  ([] original-ricker) ; just returns the function (of w)
  ([w] (* w (m/exp (- 1 w)))))

(defn ricker
  "Function from Ricker 1954 \"Stock and recruitment\" according to
  https://en.wikipedia.org/wiki/Ricker_model), or May and Oster 1976
  _American Naturalist_ \"Bifurcations and Dynamic Complexity in Simple
  Ecological Models\", table 1 equation 1.  If N is a non-zero integer in
  [0,K], result will be a double in the same range."
  ([K r] (partial ricker K r))
  ([K r N] (* N (m/exp (* r (- 1 (/ N K)))))))

;; This could be made using normalize, but it should be considered a 
;; basic function, so it's worth defining directly.
(defn normalized-ricker
  "Function from Ricker 1954 \"Stock and recruitment\"
  (https://en.wikipedia.org/wiki/Ricker_model), or May and Oster 1976
  _American Naturalist_ \"Bifurcations and Dynamic Complexity in Simple
  Ecological Models\", table 1 equation 1. Rather than using N for absolute
  population size, and K for carrying capacity, we use equation (3) on p.
  577 of May and Oster, using x=N/K, or N=Kx."
  ([r] (partial normalized-ricker r))
  ([r x] (* x (m/exp (* r (- 1 x))))))

(comment
  ;; Experiments with different ways to define a normalized Ricker function.
  (def rick (ricker 50 3.0))
  (def nick (normalize rick 50)) ; note K is the same for rick and normalize

  ;; These produce nearly identical results (the difference is very small):
  (take 27 (iterate (normalized-ricker 3.0) 0.85))
  ;; These produce identical results:
  (take 27 (iterate nick 0.85))
  (map #(/ % 50) (take 27 (iterate rick (* 0.85 50))))

  (map #(* % 50) (take 27 (iterate nick 0.85)))
  (rationalize (double 1/50))
)

(comment
  ;; Experiments with ceiled, floored, etc.

  (def cr (ceiled (ricker 10000 3.0143)))
  (def cf (floored (ricker 10000 3.0143)))

  (def init 10001)

  (take 20 (iterate (ricker 10000 3.0143) init))
  (take 20 (iterate cr init))
  (take 20 (iterate cf init))

  (def init 10.513)

  (def cr100 (ceiled (ricker 100 3.0143)))
  (def cf100 (floored (ricker 100 3.0143)))

  (take 20 (iterate (ricker 100 3.0143) init))
  (take 2000 (iterate (ricker 100 3.0143) init))
  (take 20 (iterate cr100 init))
  (take 20 (iterate cf100 init))

  ;; With this init:
  ;; ricker is nearly periodic but it's unstable, and eventually starts
  ;; looking chaotic.
  ;; cr looks more chaotic than ricker, from the start, though.
  ;; cf goes straight to K=100.
  (def init 99.9999)

  (def init 0.9) ; cf doesn't go straight to zero
  (def init 0.05) ; cf doesn't go straight to zero: next val is 1.0172069392401901
  (def init 0.04) ; cf goes straight to zero: next val is 0.8140108817154104
  (def init 0.01) ; cf goes straight to zero: next val is 0.20368682913519412

  (def init 0.91)

  (def nr (iterate (normalized-ricker 3.0143) init))
  (def nrr1000 (iterate (rounded-to-ratl 1000 nr) init))
  (def nrf1000 (iterate (floored-to-ratl 1000 nr) init))
  (def nrc1000 (iterate (ceiled-to-ratl 1000 nr) init))

  (take 20 nr)
  (take 20 nrr1000)
  (take 20 nrf1000)
  (take 20 nrc1000)





)



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Other functions that come up in dynamical systems lit

(defn scaled-exp
  "Multiplies lambda times e to the x."
  ([lambda] (partial scaled-exp lambda))
  ([lambda x] (* lambda (m/exp x))))


;; FIXME: width calc isn't working right
(defn tent
  "Returns a tent function on [0,2] with a max value y=height at x=width/2."
  [height width]
  (fn [x]
    (+ height (* height (- (abs (- x (/ width 2.0))))))))
