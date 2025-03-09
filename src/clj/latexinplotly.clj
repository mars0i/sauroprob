;; Based on
;; https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/.E2.9C.94.20LaTeX.20.20in.20TMD.20fields.20into.20Plotly.20legends.3F/near/504227110
(ns latexinplotly
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]))

(comment
  ^:kindly/hide-code
  (kind/hiccup
    [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

  ^:kindly/hide-code
  (kind/hiccup
    [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-chtml-full.js"}])

  ^:kindly/hide-code
  (kind/hiccup
    [:script 
     "window.MathJax = {
     options: {
     ignoreHtmlClass: 'tex2jax_ignore',
     processHtmlClass: 'tex2jax_process'
     },
     tex: {
     autoload: {
     color: [],
     colorv2: ['color']
     },
     packages: {'[+]': ['noerrors']}
     },
     loader: {
     load: ['[tex]/noerrors']
     }
     }}])"])
  ^:kindly/hide-code
  (kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-chtml.js" :id "MathJax-script"}])
)

^:kindly/hide-code
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

;; This is a test of the effects of LaTeX $x$ !

(kind/tex "\\mbox{Here is some math: } x^2=\\alpha")


(-> (tc/concat
      (tc/dataset {:x [0 1], :y [0 1], :fun "$y=x^2$"})
      (tc/dataset {:x [0 1], :y [1 0], :fun "y=-x"}))
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    (plotly/plot)
    (assoc-in [:data 0 :name] "y<sup>2</sup> = -x")
    (assoc-in [:data 1 :name] "$y^2 = -x$"))

(comment
;; Here is some LaTeX: $\sum_{i=0}^\infty \frac{x^i}{N}$
)

