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
  pair containing the value and the period (current index - previous) is
  returned.  If the end of the sequence is encountered with no repeats,
  nil is returned.  (Note that the test for recurrence uses a clojure
  map, which uses the function 'hash' to determine identity.  This means
  that numbers that appear to be equal but that are of different numeric
  data types may or may not be treated as identical.  The user should 
  insure that elements in the sequence are all of the same type.)"
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
  (def xs4 (logistic-vals 4 0.5))
  (take 10 xs4)
  (find-cycle2 xs4)
  (def xs (concat [7] (range 5) (range 5 1 -1)))
  (find-cycle2 xs)
  (find-cycle2 (range 1000))

  (def xs4 (logistic-vals 4 0.76))
  (take 50 xs4)
) 
