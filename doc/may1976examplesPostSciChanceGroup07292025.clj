^:kindly/hide-code
(ns iterfreqs
  (:require ;[clojure.math :as m]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]
            [sauroprob.iterfreqs-fns :as fns]))

^:kindly/hide-code
;; Make LaTeX work in Plotly labels:
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

^:kindly/hide-code
;(def n-cob 8)

(def n-dist-iters 10000)
(def n-seq-iters 100)
(def x-max 1.0)
(def init-x 0.91)


^:kindly/hide-code
(kind/md "$X_{t+1} = aX_t(1-X_t)$")


^:kindly/hide-code
(let [a 2.0
      f (um/logistic a)
      comps [1]]
  (fns/plots-grid {:x-max x-max
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x init-x
                   :n-cobweb 8
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters
                   :intro-label-md (str "$a=" a ":$") 
                  }))

^:kindly/hide-code
(let [a 3.0
      f (um/logistic a)
      comps [1]]
  (fns/plots-grid {:x-max x-max
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x init-x
                   :n-cobweb 12
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters
                   :intro-label-md (str "$a=" a ":$") 
                  }))

^:kindly/hide-code
(let [a 3.5
      f (um/logistic a)
      comps [1]]
  (fns/plots-grid {:x-max x-max
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x init-x
                   :n-cobweb 20
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters
                   :intro-label-md (str "$a=" a ":$") 
                  }))

^:kindly/hide-code
(let [a 3.55
      f (um/logistic a)
      comps [1]]

  (fns/plots-grid {:x-max x-max
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x init-x
                   :n-cobweb 15
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters
                   :intro-label-md (str "$a=" a ":$") 
                  }))

^:kindly/hide-code
(let [a 3.8
      f (um/logistic a)
      comps [1]]
  (fns/plots-grid {:x-max x-max
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x init-x
                   :n-cobweb 20
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters
                   :intro-label-md (str "$a=" a ":$") 
                  }))

^:kindly/hide-code
(let [a 4.0
      f (um/logistic a)
      comps [1]]
  (fns/plots-grid {:x-max x-max
                   :fs (map (partial msc/n-comp f) comps)
                   :labels (map (fn [n] (str "$f^" n "$")) comps)
                   :init-x init-x
                   :n-cobweb 20
                   :n-seq-iterates n-seq-iters
                   :n-dist-iterates n-dist-iters
                   :intro-label-md (str "$a=" a ":$") 
                  }))
