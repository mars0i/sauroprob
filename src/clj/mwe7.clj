(ns mwe7
  (:require [fastmath.stats :as fs]
            [clojisr.v1.r :as R]))

(R/require-r '[stats :refer [ks.test]])
(R/require-r '[dgof :refer [ks.test]])

(defn ks-tests
  [xs ys & {:keys [exact]}]
  {:rstats-result (R/r->clj (r.stats/ks-test xs ys :exact exact))

   :dgof-result (R/r->clj (r.dgof/ks-test xs ys :exact exact))

   :fastmath-result (fs/ks-test-two-samples xs ys {:method
                                                   (if exact
                                                     :exact
                                                     :approximate)})})

(def logistic4 (fn [x] (* 4 x (- 1 x))))

(let [n 516]
  (ks-tests (take n (iterate logistic4 0.14))
            (take n (iterate logistic4 0.3085937081153858))
            :exact true)) ; causes NaN for p-value with older versions of fastmath.stats
