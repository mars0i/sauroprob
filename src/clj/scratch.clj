^:kindly/hide-code
;; (Based on https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/.E2.9C.94.20LaTeX.20.20in.20TMD.20fields.20into.20Plotly.20legends.3F/near/504227110)

^:kindly/hide-code
(ns latexinplotly
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]
            [fastmath.random :as fr]
            [tablecloth.api :as tc]))


(def distr (fr/distribution :real-discrete-distribution 
                            {:data [4.2 1.3 1.1 17.54 88.9 32.77 20.1 18]}))

;; I think the idea is that the first arg is what you're passing to the cdf
;; fn.  You're asking what is the prob of values less than or equal to that.
(def cd (partial fr/cdf distr))
;; But what's the optional second arg?

(cd 20)
(cd 88.8)

(take 30 (map cd (range 0 100 0.5)))

;; And then I think I plot that.
;; i.e. so I'm not plotting steps between points, but just
;; plotting a bunch of points run through the cdf.

(fr/pdf distr 1.100000000)
