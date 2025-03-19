^:kindly/hide-code
(ns iterfreqs2
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

(def hist-iterates 100000)

;; Better:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.901
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates hist-iterates}))

^:kindly/hide-code
(def r30iterates (iterate (um/normalized-ricker 3.0) 0.901))
^:kindly/hide-code
(def r30dist (fr/distribution :real-discrete-distribution {:data (take hist-iterates r30iterates)}))
^:kindly/hide-code
(def r30cdf (partial fr/cdf r30dist))
^:kindly/hide-code
(def r30xs (range 0 3 0.001))
^:kindly/hide-code
(let [r30cdf-ys (map r30cdf r30xs)]
  (-> (tc/dataset {:x r30xs
                   :y r30cdf-ys})
      (plotly/layer-line {:=x :x, :=y, :y})
      plotly/plot))


;; Let's try a different init x:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.4
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates hist-iterates}))



;; Again:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.35
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates hist-iterates}))

;; Again again:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 0.75
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates hist-iterates}))

;; This init is the fixed point:
(let [f (um/normalized-ricker 3.0)
      comps [1]]
  (fns/three-plots {:x-max 4
                    :fs (map (partial msc/n-comp f) comps)
                    :labels (map (fn [n] (str "$f^" n "$")) comps)
                    :init-x 1
                    :n-cobweb 30
                    :n-plot-iterates 400
                    :n-hist-iterates hist-iterates}))

