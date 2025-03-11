^:kindly/hide-code
;; (Based on https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/.E2.9C.94.20LaTeX.20.20in.20TMD.20fields.20into.20Plotly.20legends.3F/near/504227110)

^:kindly/hide-code
(ns latexinplotly
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]))

(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

^:kindly/hide-code
;; Based on https://community.plotly.com/t/latex-not-working/1735/2?u=mars0i
;(kind/html "<script type=\"text/javascript\" async src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_SVG\">")
;(kind/html "<script type=\"text/javascript\" async src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AM_CHTML\">")

;; ## Plots

;; This is some kind of test.

(-> (tc/concat
      (tc/dataset {:x [0 1], :y [0 1], :fun "$y=x^2$"})
      (tc/dataset {:x [0 1], :y [1 0], :fun "y=-x"}))
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    (plotly/plot)
    (assoc-in [:data 1 :name] "$y^2 = -x$"))

;; ## $\LaTeX$

;; This is a test of the effects of LaTeX: $\sum_{i=0}^\infty \frac{x^i}{N}$ ! That was a sum.

;; $\text{This is another test of the effects of }\LaTeX\!: \sum_{i=0}^\infty \frac{x^i}{N}\text{ !}$

(comment
  (require '[scicloj.clay.v2.api :as clay])
  (clay/make! {:source-path ["src/clj/latexinplotly.clj"] :format [:quarto :html]})
  (clay/make! {:source-path ["src/clj/latexinplotly.clj"] :format [:html]})
)
