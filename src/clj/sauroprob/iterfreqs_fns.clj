;; Ad-hoc functions for use in iterfreqs.clj
(ns sauroprob.iterfreqs-fns
  (:require ;[clojure.math :as m]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [fastmath.random :as fr]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]))

;; Make LaTeX work in Plotly labels:
;(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])
;; I'm stopping using this because when labels are long, the Plotly
;; legend interferes with the plot.  See mwe5.clj for details.

;; Used to generate steps when we need to plot a function
;; over arbitrary x values.
(def x-plot-increment 0.001)

;; Previously I used "iter-lines" for the paths that make something into a
;; cobweb diagram: https://en.wikipedia.org/wiki/Cobweb_plot
;; (I didn't know the term for this.)

;; TIP: Helper function for three plots that's also intended to be used on its own.
(defn plot-fns-with-cobweb
  "Displays a plot of a function and compositions of itself with itself,
  possibly with iteration lines.
  Parameters:
  x-max:  max x val in function plot (also max y)
  fs:     functions to plot
  labels: labels corresponding to functions
  init-x: initial x val for iterations--Uses the first element in fs.
  n-cobweb:  how many iteration lines in fn plot"
  [& {:keys [x-max  ; max x val in function plot (also max y)
             fs     ; functions to plot
             labels ; labels corresponding to functions
             init-x ; initial x val for iterations--Uses the first element in fs.
             n-cobweb]}] ; number of cobweb line pairs
  (let [basef (first fs)]
    (-> (tc/concat (sp/cobweb-dataset init-x n-cobweb :fun "iteration" basef)
                   (tc/dataset {:x [0 x-max], :y [0 x-max], :fun "y=x"}) ; Removed LaTeX since Plotly bug "$y=x$"
                   (apply tc/concat ; maybe there's a more elegant way to do this
                          (map (fn [label f] (sp/fn2dataset [0 x-max] :fun label f))
                               labels fs)))
        ;(plotly/base {:=width 550})
        (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
        (sp/equalize-display-units) ; runs plotly/plot, which is needed for next lines
        (sp/set-line-width 0 1.5)
        (sp/set-line-dash 0 "dot")
        (sp/set-line-width 1 1.0)
        (sp/set-line-dash 1 "dash"))))


;; TIP:
;; This is helper function for three-plots, but can be used alone. It's a 
;; simple function; if more flexibility is needed for standalone use, it
;;  would be better to write a separate function or make a plot by hand 
;; rather than generalizing this one.
(defn plot-iterates
  "Plot lines connecting n iterates as y values with x representing number of
  iterations.  iterates should not be infinite."
  [iterates]
  (-> (tc/dataset {:x (range (count iterates))
                   :y iterates})
      ;(plotly/base {:=width 900})
      (plotly/layer-line {:=x :x, :=y, :y})
      plotly/plot
      (assoc-in [:data 0 :line :width] 1)))


;; TIP:
;; This is helper function for three-plots, but can be used alone. It's a 
;; simple function; if more flexibility is needed for standalone use, it
;;  would be better to write a separate function or make a plot by hand 
;; rather than generalizing this one.
(defn plot-iterates-histogram
  "Plot the values of n iterates in a histogram. iterates should be
  not be infinite."
  [iterates]
  (-> (tc/dataset {:x iterates})
      ;(plotly/base {:=width 800})
      (plotly/layer-histogram {:=x :x, :=histogram-nbins 200})
      plotly/plot
      (assoc-in [:data 0 :line :width] 0.7)))

;; cf. https://clojurians.zulipchat.com/#narrow/channel/203279-scicloj-org/topic/Empirical.20CDF.20in.20fastmath.3F
;; From https://generateme.github.io/fastmath/clay/random.html#discrete:
;; There are four discrete distributions:
;;     :enumerated-int for integers, backed by Apache Commons Math
;;     :enumerated-real for doubles, backed by Apache Commons Math
;;     :integer-discrete-distribution - for longs, custom implementation
;;     :real-discrete-distribution - for doubles, custom implementation
;; Please note:
;;     Apache Commons Math implementation have some issues with iCDF.
;;     :integer-discrete-distribution is backed by clojure.data.int-map
;; [There's also :empirical, which uses a histogram as the basis for the distribution.]
(defn iterates-to-cdf-dataset
  "Constructs an empirical cumulative distribution dataset from iterates."
  [x-max iterates]
  (let [x-min 0
        empir-dist (fr/distribution :real-discrete-distribution ; or :enumerated-real ?
                                    {:data iterates})
        empir-cdf (partial fr/cdf empir-dist)
        xs (range x-min x-max x-plot-increment)
        ys (map empir-cdf xs)]
    (tc/dataset {:x xs :y ys})))

(defn plot-cdf
  "Constructs an empirical cumulative distribution function from iterates
  and plots it from x-min to x-max."
  [x-max iterates]
    (-> (iterates-to-cdf-dataset x-max iterates)
        (plotly/layer-line {:=x :x, :=y, :y})
        plotly/plot
        (assoc-in [:data 0 :line :width] 1.0))) ;)


;; TODO Should this be changed to allow layering of arbitrary plots in the
;; first plot, rather than only allowing numbers of function plots to be
;; added?  Or is the latter sufficiently general?
(defn plots-column
  "Displays a plot of a function and compositions of itself with itself,
  possibly with iteration lines; a plot of values against (discrete) time
  generated by iteration, and a histogram of those values. See comments for
  details about args.  All functions in fs should take exaclty one
  argument.  The function used to iterate should be the first element in fs.
  Parameters:
  x-max:  max x val in function plot (also max y)
  fs:     functions to plot
  labels: labels corresponding to functions
  init-x: initial x val for iterations--Uses the first element in fs.
  n-cobweb:  how many iteration lines in fn plot
  n-seq-iterates:  how many iterations in plot of iteration values
  n-dist-iterates:  how many iterations in histogram?"
  [& {:keys [x-max  ; max x val in function plot (also max y)
             fs     ; functions to plot
             labels ; labels corresponding to functions
             init-x ; initial x val for iterations--Uses the first element in fs.
             n-cobweb ; how many cobweb line pairs in fn plot
             n-seq-iterates ; how many iterations in plot of iteration values
             n-dist-iterates] ; how many iterations in histogram?
      :as args}] ; how many iterations to collect in histogram
  (let [basef (first fs)
        iterates (iterate basef init-x)
        seq-iterates (take n-seq-iterates iterates)
        dist-iterates (take n-dist-iterates iterates)]
    (kind/fragment [;(kind/md ["Plot of the function, with sample iterations beginning from " init-x ":"])
                    (plot-fns-with-cobweb args) ; unused args can be ignored
                    ;(kind/md ["Plot of a sequence of values of the function beginning from " init-x ":"])
                    (plot-iterates seq-iterates)
                    ;(kind/md ["Distribution of values beginning from " init-x ":"])
                    (plot-iterates-histogram dist-iterates)
                    ;(kind/md ["cdf of values beginning from " init-x ":"])
                    (plot-cdf x-max dist-iterates)])))

(defn plots-grid
  "Displays a plot of a function and compositions of itself with itself,
  possibly with iteration lines; a plot of values against (discrete) time
  generated by iteration, and a histogram of those values. See comments for
  details about args.  All functions in fs should take exaclty one
  argument.  The function used to iterate should be the first element in fs.
  Parameters:
  x-max:  max x val in function plot (also max y)
  fs:     functions to plot
  labels: labels corresponding to functions
  init-x: initial x val for iterations--Uses the first element in fs.
  n-cobweb:  how many iteration lines in fn plot
  n-seq-iterates:  how many iterations in plot of iteration values
  n-dist-iterates:  how many iterations in histogram?"
  [& {:keys [x-max  ; max x val in function plot (also max y)
             fs     ; functions to plot
             labels ; labels corresponding to functions
             init-x ; initial x val for iterations--Uses the first element in fs.
             n-cobweb ; how many cobweb line pairs in fn plot
             n-seq-iterates ; how many iterations in plot of iteration values
             n-dist-iterates] ; how many iterations in histogram?
      :as args}] ; how many iterations to collect in histogram
  (let [basef (first fs)
        iterates (iterate basef init-x)
        seq-iterates (take n-seq-iterates iterates)
        dist-iterates (take n-dist-iterates iterates)
        cobweb-plot (plot-fns-with-cobweb args) ; unused args can be ignored
        seq-plot (plot-iterates seq-iterates)
        hist-plot (plot-iterates-histogram dist-iterates)
        cdf-plot (plot-cdf x-max dist-iterates)
        combo-plot (plotly/plot
                     {:layout (:layout cobweb-plot)         ; combweb s/b last:
                      :data (vec (mapcat :data [seq-plot cdf-plot hist-plot cobweb-plot]))})
        n-traces (count (:data combo-plot))] ; depends on what's in cobweb-plot
    (-> combo-plot 
        (assoc-in [:layout :width] 1000)
        (assoc-in [:layout :height] 600)
        (assoc-in [:layout :grid] {:rows 2, :columns 2, :pattern "independent"})
        (sp/set-subplot-order [0] 2)     ; iterating the function over n steps
        (sp/set-subplot-order [1] 3)     ; the cumulative dist function
        (sp/set-subplot-order [2] 4)     ; histogram
        (sp/set-subplot-order (msc/irange 3 n-traces) 1) ; remaining traces are for cobweb plot
        (assoc-in [:layout :xaxis]  {:domain [0, 0.25]})
        (assoc-in [:layout :xaxis3] {:domain [0, 0.25]})
        (assoc-in [:layout :xaxis2] {:domain [0.3, 1.0]}) ; why are axis bars shifted left?
        (assoc-in [:layout :xaxis4] {:domain [0.3, 1.0]}))))

