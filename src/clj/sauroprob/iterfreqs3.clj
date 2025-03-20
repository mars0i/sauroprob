^:kindly/hide-code
(ns iterfreqs3
  (:require ;[clojure.math :as m]
            [fastmath.random :as fr]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))

(def n-iterates 100000)

;; Better:
(let [f um/logistic-4
      comps [1]]
  (fns/plots {:x-max 1
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.901
                    :n-cobweb 30
                    :n-seq-iterates 400
                    :n-dist-iterates n-iterates}))

^:kindly/hide-code
(def l4iterates (iterate um/logistic-4 0.901))
^:kindly/hide-code
(def l4dist (fr/distribution :real-discrete-distribution {:data (take n-iterates l4iterates)}))
^:kindly/hide-code
(def l4cdf (partial fr/cdf l4dist))

^:kindly/hide-code
(def l4xs (range 0 1 0.001))
^:kindly/hide-code
(let [l4cdf-ys (map l4cdf l4xs)]
  (-> (tc/dataset {:x l4xs
                   :y l4cdf-ys})
      (plotly/layer-line {:=x :x, :=y, :y})
      plotly/plot
      (assoc-in [:data 0 :line :width] 0.75)))

(fns/plot-cdf 1 (take 100000 l4iterates))

;; Let's try a different init x:
(let [f um/logistic-4
      comps [1]]
  (fns/plots {:x-max 1
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.4
                    :n-cobweb 30
                    :n-seq-iterates 400
                    :n-dist-iterates n-iterates}))



;; Again:
(let [f um/logistic-4
      comps [1]]
  (fns/plots {:x-max 1
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.35
                    :n-cobweb 30
                    :n-seq-iterates 400
                    :n-dist-iterates n-iterates}))

;; Again again:
(let [f um/logistic-4
      comps [1]]
  (fns/plots {:x-max 1
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.75
                    :n-cobweb 30
                    :n-seq-iterates 400
                    :n-dist-iterates n-iterates}))

;; This init is the fixed point:
(let [f um/logistic-4
      comps [1]]
  (fns/plots {:x-max 1
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 1
                    :n-cobweb 30
                    :n-seq-iterates 400
                    :n-dist-iterates n-iterates}))

