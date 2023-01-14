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




(comment
  (def xs4 (logistic-vals 4 0.76))
  (take 50 xs4)

) 
