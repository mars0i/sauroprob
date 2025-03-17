^:kindly/hide-code
;; (Based on https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/.E2.9C.94.20LaTeX.20.20in.20TMD.20fields.20into.20Plotly.20legends.3F/near/504227110)

(ns latexinplotly2
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]))


;; ## Setup

(comment
  (require '[scicloj.clay.v2.api :as clay])

  (clay/make! {:source-path ["src/clj/latexinplotly2.clj"]
               :format [:html]})

  (clay/make! {:source-path ["src/clj/latexinplotly2.clj"]
               :format [:quarto :html]})

  (clay/make! {:source-path ["src/clj/latexinplotly2.clj"]
               :format [:quarto :html]
               ;; This is the extra line that allows LaTeX in Plotly with Quarto:
               :quarto {:format {:html {:html-math-method :katex}}}})
)

;; This makes LaTeX in Plotly labels work:
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])
;; Note this apparently must be an instance of MathJax 2.  MathJax v3 doesn't
;; seem to work, although some documentation suggests it should.

;; ## A plot

(-> (tc/concat
      (tc/dataset {:x [0 1], :y [0 1], :fun "$y_1=x_1$"})
      (tc/dataset {:x [0 1], :y [1 0], :fun "$y^2 = -z^3$"}))
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun}))

;; ## LaTeX-in-comments example

;; Did $\sum_{j=0}^\infty \frac{1}{2^j}$ appear on a separate line here? (That's what happens without Quarto.)
