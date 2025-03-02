^:kindly/hide-code
(ns sauroprob.plotly
  (:require [clojure.math :as m] ; new in Clojure 1.11 
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            ;[scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.string :as st]
            [utils.math :as um]
           ))


(def plot-steps 400)

;; [xmin x-max] is the first arg, for use with -> , because it's likely
;; that different fns will share same x coords.
(defn fn2dataset
  "Generates a TMD dataset of points in which the coordinates are (x, f(x))
  from x-min to x-max, with an additional field named catkey, with a single
  value catval.  This key/val pair can be used to identify these coordinates
  after they are merged into another dataset."
  [[x-min x-max] catkey catval f & {:keys [steps]}] 
  (let [x-increment (/ (double (- x-max x-min))
                       (or steps plot-steps))
        xs (msc/irange x-min x-max x-increment)
        ys (map f xs)]
    (-> {:x xs, :y ys, catkey catval}
        tc/dataset)))

(defn equalize-display-units
  "Given a Tableplot plot (in either Hanami-key form or full Plotly EDN),
  adds Plotly settings to force the displayed x and y units to be equal.
  (Note if the plot's display dimensions are too large in one dimension,
  there will be extra space in the plot outside of the plotted data.)"
  [plot]
  (-> plot
      plotly/plot
      (assoc-in [:layout :yaxis :scaleanchor] :x)
      (assoc-in [:layout :yaxis :scaleratio] 1)))

;(defn next-iter-seg
;  [f x]
;  {:x [x x]
;   :y [x (f x)]})

(defn next-vert-seg
  "ADD DOCSTRING"
  [[x next-x]]
  {:x [x x]
   :y [x next-x]})

;; We only construct vertical segments explicitly; by chaining
;; these together, the lines that connect them are the horizontal
;; segments.
(defn iter-lines
  "ADD DOCSTRING"
  [init-x iters catkey catval f]
  (->> (iterate f init-x)
       (take iters)
       (partition 2 1)
       (map next-vert-seg)
       (apply merge-with into)
       (#(assoc % catkey catval)) ; since threading last
       tc/dataset))

;; Does same thing, but stylistically different, maybe less efficient:
(defn iter-lines-alt
  "ADD DOCSTRING"
  [init-x iters catkey catval f]
  (let [f-iterates (take iters (iterate f init-x))
        next-pairs (partition 2 1 f-iterates)
        vert-segs (map next-vert-seg next-pairs)
        coords (apply merge-with into vert-segs)]
    (-> coords
        (assoc catkey catval)
        tc/dataset)))

(comment
  (iter-lines1 0.75 5 :fun "ya" (um/normalized-ricker 2.7))
  (iter-lines 0.75 5 :fun "ya" (um/normalized-ricker 2.7))
)

(def rickers
  (let [r 2.7
        f (um/normalized-ricker r)]
    (tc/concat
      (tc/dataset {:x [0 2.1], :y [0 2.1], :fun "y=x"})
      (iter-lines 0.75 8 :fun "iter" f)
      (fn2dataset [0 3] :fun "f" f)
      (fn2dataset [0 3] :fun "f^2" (msc/n-comp f 2))
      (fn2dataset [0 3] :fun "f^4" (msc/n-comp f 4)))))

;; It would be simpler to embed the HTML in the vals, but this illustrates
;; the option of adding them in the final stage.
(-> rickers
    (plotly/base {:=height 420 :=width 700})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    (plotly/plot)
    (assoc-in [:data 0 :line :dash] "dash") ; https://plotly.com/javascript/reference/scatter/#scatter-line-dash 
    (assoc-in [:data 0 :name] "<em>y=x</em>") ; https://plotly.com/javascript/reference/scatter/#scatter-name
    (assoc-in [:data 1 :line :dash] "dot")
    (assoc-in [:data 2 :line :width] 3) ; default is 2.  https://plotly.com/javascript/reference/scatter/#scatter-line-width
    (assoc-in [:data 2 :name] "<em>f(x)=xe<sup>r(1-x)</sup></em>")
    (assoc-in [:data 3 :name] "<em>f<sup>2</sup></em>")
    (assoc-in [:data 4 :name] "<em>f<sup>4</sup></em>")
    (equalize-display-units) ; If display dimensions don't fit data, extra space in plot
    ;(kind/pprint)
   )



(def three
  (tc/concat
    (tc/dataset {:x [-4 1], :y [-4 1], :fun :y=x})
    (fn2dataset [-4.0 1.0] :fun :base (partial um/scaled-exp (- m/E)))
    (fn2dataset [-4.0 1.0] :fun :comp2 (msc/n-comp (partial um/scaled-exp (- m/E)) 2))
    (fn2dataset [-4.0 1.0] :fun :comp3 (msc/n-comp (partial um/scaled-exp (- m/E)) 3))))

(-> three
    (plotly/base {:=height 600 :=width 600})
    (plotly/layer-point {:=x :x, :=y, :y, :=color :fun, :=size :fun, :=mark-opacity 0.2,
                        :=name "Yow"})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    equalize-display-units
    (plotly/plot)
    ;(kind/pprint)
    )

(kind/tex "x^2=\\alpha")

(def f2 (kind/tex "\\sum_{i=0}^\\infty \\mu \\frac{f^2}{Q_{32}}"))

f2

(kind/pprint f2)

(meta f2)

(def lf2 ^{:kindly{:kind :kind/tex, :options nil}}
  [
  "<math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">
  <munderover>
    <mo data-mjx-texclass=\"OP\">&#x2211;</mo>
    <mrow data-mjx-texclass=\"ORD\">
      <mi>i</mi>
      <mo>=</mo>
      <mn>0</mn>
    </mrow>
    <mi mathvariant=\"normal\">&#x221E;</mi>
  </munderover>
  <mi>&#x3BC;</mi>
  <mfrac>
    <msup>
      <mi>f</mi>
      <mn>2</mn>
    </msup>
    <msub>
      <mi>Q</mi>
      <mrow data-mjx-texclass=\"ORD\">
        <mn>32</mn>
      </mrow>
    </msub>
  </mfrac>
</math>"])

lf2
(meta lf2)


(def two
  (let [λ1 (- (- m/E) 2.0)
        f1 (partial um/scaled-exp λ1)
        λ2 (- (- m/E) 3.0)
        f2 (partial um/scaled-exp λ2)]
    (tc/concat
      (tc/dataset {:x [-7 0.5], :y [-7 0.5], :fun "y=x"})
      (fn2dataset [-7.0 0.5] :fun "f<sub>1</sub>(x)=λ<sub>1</sub>e<sup>x</sup>" f1)
      (fn2dataset [-7.0 0.5] :fun "f<sub>1</sub><sub>2</sub>" (msc/n-comp f1 2))
      (fn2dataset [-7.0 0.5] :fun "f<sub>2</sub>(x)=λ<sub>1</sub>e<sup>x</sup>" f2)
      (fn2dataset [-7.0 0.5] :fun "f<sub>2</sub><sub>2</sub>" (msc/n-comp f2 2))
    )))

(-> two
    (plotly/base {:=height 600 :=width 600})
    (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
    (equalize-display-units)
    )

(let [λ (- m/E)
      f (partial um/scaled-exp λ)]
  (-> (tc/concat
        (tc/dataset {:x [-7 0.5], :y [-7 0.5], :fun "<em>y</em>=<em>x</em>"})
        (fn2dataset [-7.0 0.5] :fun "f(x)=λe<sup>x</sup>" f))
      (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
      (equalize-display-units)))


;; This illustrates a method for replacing values at the last step in order
;; to change how they are displayed.  Using an array index seems fragile.
(let [λ (- m/E)
      f (partial um/scaled-exp λ)]
  (-> (tc/concat
        (tc/dataset {:x [-7 0.5], :y [-7 0.5], :fun "y=x"})
        (fn2dataset [-7.0 0.5] :fun "scaled-exp" f))
      (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
      (plotly/plot)
      ;; This works but it's fragile, and only replaces one value:
      (assoc-in [:data 1 :name] "f(x)=λe<sup>x</sup>")
      ;; This works but it's too complicated (cleaner with pre-defined fns):
      ;(update :data (fn [v] (mapv
      ;                        (fn [m] (update m :name #(if (= % "scaled-exp") "f(x)=λe<sup>x</sup>" %)))
      ;                        v)))                    ;; use `cond` for more replacements
      (kind/pprint)
      ))
