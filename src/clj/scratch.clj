^:kindly/hide-code
;; (Based on https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/.E2.9C.94.20LaTeX.20.20in.20TMD.20fields.20into.20Plotly.20legends.3F/near/504227110)

^:kindly/hide-code
(ns latexinplotly
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]
            [tablecloth.api :as tc]))



(comment
  (require '[fastmath.core :as fc])
  (require '[fastmath.random :as fr])

  (fr/distribution :gamma)

  (def nums         [5 7 9 2 3 4 1 1 2 3 2 4 2 3 5])
  (sort nums)     ; (1 1 2 2 2 2 3 3 3 4 4 5 5 7 9)
  ;; returns the indexes of the elements *in the original sequence*, after
  ;; sorting the sequence.
  (fc/order nums) ; (6 7 3 8 10 12 4 9 13 5 11 0 14 1 2)
  (def nums         [5 7 9 2 3 4 1 1 2 3 2 4 2 3 5])
  (map-indexed vector nums)
  ; ([0 5] [1 7] [2 9] [3 2] [4 3] [5 4] [6 1] [7 1] [8 2] [9 3] [10 2] [11 4] [12 2] [13 3] [14 5])
  (sort-by second < (map-indexed vector nums))
  ; ([6 1] [7 1] [3 2] [8 2] [10 2] [12 2] [4 3] [9 3] [13 3] [5 4] [11 4] [0 5] [14 5] [1 7] [2 9])
  (def si (fc/order nums))

  (distinct nums)
  (count nums)

  ;; This works because vectors are functions of indexes
  (map 
    (vec (concat 
           (repeat 5 (/ 1.0 5))
           (repeat 10 (/ -1.0 10))))
    si)

  (vec '(1 2))

  (defn subs-seqs
    [xs]
    (map take 
         (range)
         (repeat (count xs) xs)))

  (defn subs-seqs2
    [xs]
    (let [n (count xs)]
      (loop [m 0, acc []]
        (if (= m n)
          acc
          (recur (inc m) 
                 (conj acc (take m xs)))))))

  (subs-seqs (range 6))
  (subs-seqs2 (range 6))

  (defn ecdf
    [xs]
    (let [sorted (sort xs)
          subseqs (subs-seqs sorted)
          counts (map count subseqs)] ; incorrect there are dupes?
      (map vector sorted counts)))
  
  (ecdf [5 7 1 17 18 14 11])
  (ecdf [5 7 7 1 17 18 14 11]) ; not right.  Or is it?  No.


)
