(ns sauroprob.core)

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
  keys :val for the number that cycles, :period for the period of the
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
          {:val y :period (- i prev-idx) :starts-at prev-idx}  ; old version: [y (- i prev-idx) prev-idx]
          (recur (rest ys) (inc i) (assoc seen y i)))))))

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

) 
