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

;; FIXME The period is not the size of the set, because there may be
;; an initial sequence before the recurrence begins.
;; OBSOLETE
(defn find-cycle
  "Loops through the values in sequence xs, looking for the first 
  value that has already appeared in the sequence.  If such a value
  is found, a pair containing the value and the period of the number
  of preceding values (the period) is returned.  If the end
  of the sequence is encountered with no repeats, nil is returned.
  (Note that the test for recurrence uses a clojure set, which uses
  the function hash to determine identity.  This means that numbers
  that appear to be equal but that are of different numeric data
  types may or may not be treated as identical.  To prevent problems,
  make sure elements in the sequence are all of the same data type.)"
  [xs]
  (loop [ys xs, seen #{}]
    (let [y (first ys)]
      (cond (empty? ys) nil
            (seen y)    [y (count seen)] ; FIXME
            :else (recur (rest ys) (conj seen y))))))


;; FIXME off by one. TODO: Add index of start of cycle.
(defn find-cycle2
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
          [y (- i prev-idx)] ; TODO off-by-one?
          (recur (rest ys) (inc i) (assoc seen y i)))))))

(comment
  (def xs4 (logistic-vals 4 0.5))
  (take 10 xs4)
  (find-cycle2 xs4)
  (def xs (concat (range 12) [11] (range 5 10)))
  (find-cycle2 xs)

  (def xs4 (logistic-vals 4 0.76))
  (take 50 xs4)
) 
