^:kindly/hide-code
;; (Based on https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/.E2.9C.94.20LaTeX.20.20in.20TMD.20fields.20into.20Plotly.20legends.3F/near/504227110)

^:kindly/hide-code
(ns latexinplotly2
  (:require [scicloj.clay.v2.api :as clay] ; only needed for make!
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]))


;; # setup

^:kindly/hide-code
(comment
  (clay/make! {:source-path ["src/clj/latexinplotly2.clj"]
               :format [:quarto :html]
               ;; Adding this line allows Quarto processing to include Plotly plots
               ;; while also making LaTeX inline in comments:
               :quarto {:format {:html {:html-math-method :katex}}}
               ;; However, uncommening the mathjax line to cause Plotly to
               ;; process LaTeX in labels 
               })
)

;; This makes LaTeX in Plotly labels work (without Quarto; Plotly messes up plots with Quarto):
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

;; Note this apparently must be an instance of MathJax 2. v3 doesb't
;; seem to work (although some documentation suggests it should).

;; Did $\LaTeX$ appear on a separate line here?  That's what the default
;; `kind/tex` use of KaTeX does.  Quarto, on the other hand, doesn't 
;; break lines around LaTeX in comments.

;; # a plot

(-> (tc/concat
      (tc/dataset {:x [0 1], :y [0 1], :fun "$y=x^2$"})
      (tc/dataset {:x [0 1], :y [1 0], :fun "y=-x"}))
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    (plotly/plot)
    (assoc-in [:data 1 :name] "$y^2 = -x$"))


;; # stuff

(comment
  (clay/make! {:source-path ["src/clj/latexinplotly2.clj"] :format [:quarto :html]})
  (clay/make! {:source-path ["src/clj/latexinplotly2.clj"] :format [:html]})
)

;; ^:kindly/hide-code
;; Based on https://community.plotly.com/t/latex-not-working/1735/2?u=mars0i
;;(kind/html "<script type=\"text/javascript\" async src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_SVG\">")
;;(kind/html "<script type=\"text/javascript\" async src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AM_CHTML\">")
