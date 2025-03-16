^:kindly/hide-code
;; (Based on https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/.E2.9C.94.20LaTeX.20.20in.20TMD.20fields.20into.20Plotly.20legends.3F/near/504227110)

^:kindly/hide-code
(ns latexinplotly
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.metamorph.ml.rdatasets :as rdatasets]
            [tablecloth.api :as tc]))

;; ## Setup

(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

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

;; ## Plots

;; (tc/head (rdatasets/datasets-mtcars))

(def data (-> (tc/concat
                (tc/dataset {:x [0 1], :y [0 1], :fun "y=x" :my-size 10})
                (tc/dataset {:x [0 1], :y [1 0], :fun "y=-x" :my-size 50}))
              (tc/add-columns {:line-width #(map (fn [v] (if (= v "y=x") 1 10)) (% :fun))})))

(-> (tc/dataset {:x [0 1], :y [1 0] :my-size [20 30]})
    (plotly/layer-point {:=x :x, :=y :y, :=size :my-size})
    (plotly/layer-line {:=x :x, :=y :y, :=size :my-size}))

;(-> data
;    (plotly/layer-point {:=x :x, :=y :y, :=color :fun, :=size :my-size})
;    (plotly/layer-line {:=x :x, :=y :y, :=color :fun, :=size 10})
;    (plotly/plot))


(def aplot (-> data
               ;(tc/add-columns {:line-width #(map (fn [v] (if (= v "y=x") 1 10)) (% :fun))})
               (plotly/layer-line {:=x :x, :=y :y, :=color :fun})
               ;(plotly/layer-line {:=x :x, :=y :y, :=color :fun, :=mark-size :line-width, :=size-type :quantitative})
               ;(plotly/layer-line {:=x :x, :=y :y, :=color :fun, :size [10 20]})
               ;(plotly/layer-line {:=x :x, :=y :y, :=color :fun, :=mark-size 20})
               ;(plotly/layer-point {:=x :x, :=y :y, :=color :fun, 
               ;                    :=mark-size [100 200 250 150]})
               (plotly/plot)
               (assoc-in [:data 0 :line :dash] "dot")
               ;(assoc-in [:data 0 :line :width] 25)
               (assoc-in [:data 1 :name] "$y^2 = -x$")
               ))

aplot

(kind/pprint aplot)

(comment
  (data :line-width)

  (tc/select-rows data (fn [row] (= (:fun row) "y=x")))
  (tc/select-rows data #(= (:fun %) "y=-x"))

  (clojure.repl/pst)
)

;; This is a way to set different line widths semantically.  But it's a pain.
(def bplot (-> data
               (tc/select-rows #(= (:fun %) "y=x"))
               (plotly/layer-line {:=x :x, :=y :y, :=color :fun :=mark-size 10})
               (plotly/plot)))

(def cplot (-> data
               (tc/select-rows #(= (:fun %) "y=-x"))
               (plotly/layer-line {:=x :x, :=y :y, :=color :fun :=mark-size 20})
               (plotly/plot)))

;; plotly/plot needed in next line to add kind metadata:
(def dplot (plotly/plot
             {:data (vec (concat (:data bplot) (:data cplot)))
              :layout (:layout bplot)}))
;; Is there some way to use merge-with?


dplot

;; This eats things that come after it:

;; ^:kindly/hide-code
;; Based on https://community.plotly.com/t/latex-not-working/1735/2?u=mars0i
;;(kind/html "<script type=\"text/javascript\" async src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_SVG\">")
;;(kind/html "<script type=\"text/javascript\" async src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AM_CHTML\">")



(comment
  (tc/add-columns data {:V1 #(map inc (% :x))
                        :V5 #(map (comp keyword str) (% :y))
                        :V6 11
                        :lw #(map (fn [v] (if (= v "y=x") 25 42)) (% :fun))})

  (tc/add-columns data {:lw #(map (fn [v] (if (= v "y=x") 25 42)) (% :fun))})

  ;; fails--the map brackets are necessary:
  (tc/add-columns data :lw #(map (fn [v] (if (= v "y=x") 25 42)) (% :fun)))
)
