;; Ad-hoc functions for use in iterfreqs.clj
(ns sauroprob.iterfreqs-fns
  (:require ;[clojure.math :as m]
            [scicloj.tableplot.v1.plotly :as plotly]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]
            [tablecloth.api :as tc]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.plotly :as sp]))

;; Make LaTeX work in Plotly labels:
(kind/hiccup [:script {:src "https://cdn.jsdelivr.net/npm/mathjax@2/MathJax.js?config=TeX-AMS_CHTML"}])

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
                   (tc/dataset {:x [0 x-max], :y [0 x-max], :fun "$y=x$"})
                   (apply tc/concat ; maybe there's a more elegant way to do this
                          (map (fn [label f] (sp/fn2dataset [0 x-max] :fun label f))
                               labels fs)))
        (plotly/base {:=height 400 :=width 550})
        (plotly/layer-line {:=x :x, :=y, :y :=color :fun})
        (sp/equalize-display-units) ; runs plotly/plot, which is needed for next lines
        (sp/set-line-width 0 1.5)
        (sp/set-line-dash 0 "dot")
        (sp/set-line-dash 1 "dash"))))


;; TIP:
;; This is helper function for three-plots, but can be used alone. It's a 
;; simple function; if more flexibility is needed for standalone use, it
;;  would be better to write a separate function or make a plot by hand 
;; rather than generalizing this one.
(defn plot-iterates
  "Plot lines connecting n iterates as y values with x representing number of
  iterations.  iterates should be infinite or longer than n."
  [n iterates]
  (-> (tc/dataset {:x (range n)
                   :y (take n iterates)})
      (plotly/base {:=height 400 :=width 900})
      (plotly/layer-line {:=x :x, :=y, :y})
      plotly/plot
      (assoc-in [:data 0 :line :width] 1)))


;; TIP:
;; This is helper function for three-plots, but can be used alone. It's a 
;; simple function; if more flexibility is needed for standalone use, it
;;  would be better to write a separate function or make a plot by hand 
;; rather than generalizing this one.
(defn iterates-histogram
  "Plot the values of n iterates in a histogram. iterates should be
  infinite or longer than n."
  [n iterates]
  (-> (tc/dataset {:x (take n iterates)})
      (plotly/base {:=height 600 :=width 800})
      (plotly/layer-histogram {:=x :x, :=histogram-nbins 200})))


;; TODO Should this be changed to allow layering of arbitrary plots in the
;; first plot, rather than only allowing numbers of function plots to be
;; added?  Or is the latter sufficiently general?
(defn three-plots
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
  n-plot-iterates:  how many iterations in plot of iteration values
  n-hist-iterates:  how many iterations in histogram?"
  [& {:keys [x-max  ; max x val in function plot (also max y)
             fs     ; functions to plot
             labels ; labels corresponding to functions
             init-x ; initial x val for iterations--Uses the first element in fs.
             n-cobweb ; how many cobweb line pairs in fn plot
             n-plot-iterates ; how many iterations in plot of iteration values
             n-hist-iterates] ; how many iterations in histogram?
      :as args}] ; how many iterations to collect in histogram
  (let [basef (first fs)
        iterates (iterate basef init-x)]
    (kind/fragment [(kind/md ["Plot of the function, with sample iterations beginning from " init-x ":"])
                    (plot-fns-with-cobweb args) ; unused args can be ignored
                    (kind/md ["Plot of a sequence of values of the function beginning from " init-x ":"])
                    (plot-iterates n-plot-iterates iterates)
                    (kind/md ["Distribution of values beginning from " init-x ":"])
                    (iterates-histogram n-hist-iterates iterates)])))

