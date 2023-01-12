(ns dynoprob.core)

(defn logistic
  "The logistic function with parameter r."
  [r x]
  (* r x (- 1 x)))

(def logistic-4
  "([x])
   Applies a logistic function with parameter r=4 to x."
  (partial logistic 4))

(defn logistic-vals
  "Returns a lazy sequence of values from iterating a logistic
  equation with parameter r, beginning with given initial state."
  [r initial]
  (iterate (partial logistic r) initial))

(comment
  (def xs4 (logistic-vals 4 0.25))

) 
