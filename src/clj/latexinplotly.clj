^:kindly/hide-code
;; (Based on https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/.E2.9C.94.20LaTeX.20.20in.20TMD.20fields.20into.20Plotly.20legends.3F/near/504227110)

^:kindly/hide-code
(ns latexinplotly
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]))

;; ## Setup

(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

;; ## Plots

(def aplot (-> (tc/concat
                 (tc/dataset {:x [0 1], :y [0 1], :fun "10"})
                 (tc/dataset {:x [0 1], :y [1 0], :fun "1"}))
               (plotly/layer-line {:=x :x, :=y, :y :=color :fun
                                   ;; This idea of using a function does work, but the if 
                                   ;; conditions fail.  There's no :fun key at the top level in x.
                                   ; :=mark-size (fn [x] (if (= (:fun x) "10") 10 1))
                                   ; :=mark-size (fn [x] (if (= (:name x) "10") 10 1))
                                   ; :=mark-size (fn [x] (Integer/parseInt (:fun x)))
                                   })
               (plotly/plot)
               (assoc-in [:data 1 :name] "$y^2 = -x$")
               (assoc-in [:data 0 :line :dash] "dot")
               (assoc-in [:data 0 :line :width] 1)
               ))

aplot

(kind/pprint aplot)

;; ### $\LaTeX\!\!:$

;; Here is some LaTeX, $X + Y$ which might appear on a separate line.

;; $\text{Here is some more LaTeX: }X + Y\text{.  Did it appear on a separate line?}$
;; If not, that shows how to embed text in LaTeX for KaTeX using `\text`.

(comment
;; This is a test of the effects of LaTeX $\sum_{i=0}^\infty \frac{x^i}{N}$ ! That was a sum.
  (require '[scicloj.clay.v2.api :as clay])
  (clay/make! {:source-path ["src/clj/latexinplotly.clj"] :format [:quarto :html]})
  (clay/make! {:source-path ["src/clj/latexinplotly.clj"] :format [:html]})
)

;; This eats things that come after it:

;; ^:kindly/hide-code
;; Based on https://community.plotly.com/t/latex-not-working/1735/2?u=mars0i
;;(kind/html "<script type=\"text/javascript\" async src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_SVG\">")
;;(kind/html "<script type=\"text/javascript\" async src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AM_CHTML\">")


