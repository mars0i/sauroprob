(ns mwe6
  (:require [fastmath.stats :as fs]
            [clojisr.v1.r :as R]))

(R/require-r '[stats :refer [ks.test]])
(R/require-r '[dgof :refer [ks.test]])

(R/r->clj (r.stats/ks-test (range 1000) (range 100)))
; {:statistic [0.0],
;  :p.value [1.0],
;  :alternative ["two-sided"],
;  :method ["Asymptotic two-sample Kolmogorov-Smirnov test"],
;  :data.name [".MEM$xe81be2f1e96340b9 and .MEM$x976c5742a6c84322"],
;  :exact [false]}

(R/r->clj (r.dgof/ks-test (range 1000) (range 100)))
; {:statistic [0.0],
;  :p.value [1.0],
;  :alternative ["two-sided"],
;  :method ["Two-sample Kolmogorov-Smirnov test"],
;  :data.name [".MEM$xf7132ff626b54edb and .MEM$x8e56b4112bef4910"]}

(fs/ks-test-two-samples (range 1000) (range 100))
; {:stat 0.022360679774997897,
;  :n 500.0,
;  :p-value 1.0,
;  :KS 0.022360679774997897,
;  :nx 1000,
;  :dn -0.0,
;  :sides :two-sided,
;  :dp 0.001,
;  :d 0.001,
;  :ny 1000}

;; ---
;; This is what it looks like in R.app:

;; > ks.test(0:999, 0:999)
;; 
;; 	Asymptotic two-sample Kolmogorov-Smirnov test
;; 
;; data:  0:999 and 0:999
;; D = 0, p-value = 1
;; alternative hypothesis: two-sided
;; 
;; Warning message:
;; In ks.test.default(0:999, 0:999) :
;;   p-value will be approximate in the presence of ties
;; > require(dgof)
;; Loading required package: dgof
;; 
;; Attaching package: ‘dgof’
;; 
;; The following object is masked from ‘package:stats’:
;; 
;;     ks.test
;; 
;; > ks.test(0:999, 0:999)
;; 
;; 	Two-sample Kolmogorov-Smirnov test
;; 
;; data:  0:999 and 0:999
;; D = 0, p-value = 1
;; alternative hypothesis: two-sided
;; 
;; Warning message:
;; In ks.test(0:999, 0:999) : cannot compute correct p-values with ties
