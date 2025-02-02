(ns sauroprob.core
  (:require 
            ;[clojure.math.numeric-tower :as m]
            [clojure.math :as m] ; new in Clojure 1.11 
            [oz.core :as oz]
            [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]
            [utils.json :as json]
            [utils.string :as st]
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.hanami :as sh]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(comment
  (oz/start-server!)

  ;; From https://behrica.github.io/vl-galery/convert/
  (def simple-bar-chart
    {:data {:values [{:a "A" :b 28}
                     {:a "B" :b 55}
                     {:a "C" :b 43}
                     {:a "D" :b 91}
                     {:a "E" :b 81}
                     {:a "F" :b 53}
                     {:a "G" :b 19}
                     {:a "H" :b 87}
                     {:a "I" :b 52}]}
     :description "A simple bar chart with embedded data."
     :encoding {:x {:axis {:labelAngle 0} :field "a" :type "nominal"}
                :y {:field "b" :type "quantitative"}}
     :mark "bar"})

  (oz/view! simple-bar-chart)

  (def histogram
    {:data {:url "https://raw.githubusercontent.com/vega/vega/master/docs/data/movies.json"}
     :encoding {:x {:bin true :field "IMDB Rating"} :y {:aggregate "count"}}
     :mark "bar"})

  (oz/view! histogram)

  (def my-histogram
    {:data {:values (map (fn [data-x] {:data-x data-x}) 
                (take 10000 (iterate (um/logistic 3.9) 0.1))
                ;(take 50 (iterate um/original-ricker 0.1))
                )}
     :encoding {:x {:bin {:maxbins 50} :field :data-x} :y {:aggregate "count"}}
     :mark "bar"})

  (oz/view! my-histogram)

  
  

  ;; FIXME This shows that what I got is not a density plot--not what I wanted.
  (def density (sh/vl-fn-plot "Yow" (um/logistic 4) 0.1 200))
  (oz/view! density)
  (def splot (sh/vl-plot-seq "1K" (take 20 (iterate (um/logistic 3) 0.1))))
  (oz/view! splot)
  (def mplot (sh/make-vl-spec 0.0 1.0 um/logistic [3.0] 1 [0.99] 10))
  (oz/view! mplot)

  (oz/view! (sh/make-vl-spec 0.0 1400 um/logistic-plus [1000 3.00] 1 [0.1] 40))
  
  (oz/view! (sh/make-vl-spec 0.0 1400 (um/logistic-plus 1000 3.00) [] 1 [0.1] 40))

  ;; FIXME The error here is that normalize returns a function of one arg
  ;; that is supposed to be `apply`ed to zero args to produce a function of
  ;; one arg, but apply doesn't work like that.
  (oz/view! (sh/make-vl-spec 0.0 1.0 (um/normalize um/logistic-plus 1000 2.0) [] 1 [] 1))

  (apply (fn [rx] 22) [])

  (oz/view! (sh/vl-plot-seq "1K" (take 1000 (iterate (um/logistic-plus 100000 2.57) 0.1))))

  (oz/view! (sh/make-vl-spec 0.0 5.0 um/pre-ricker [0.3679] 1 [] 1))

  (oz/view! (sh/make-vl-spec 0.0 5.0 um/original-ricker [] 1 [] 1))


  (oz/view! (sh/make-vl-spec 0.0 1.0 um/logistic [3.0] 1 [0.99] 10))

  (oz/view! (sh/make-vl-spec 0.0 1.0 um/logistic [3.0] 6
                             [0.05 0.075 0.15] 3
                             :fixedpt-x 0.5
                             :addl-plots [(sh/horiz 1.0)]))

  (def abs-spec (sh/make-vl-spec 0.0 1.0 um/tent 0.5 1 [] 3))
  (oz/view! abs-spec)

  (oz/view! (sh/make-vl-spec 0 4.5 um/foo 2.5 3 [] 14 :fixedpt-x 1.0))

  (oz/view! (sh/make-vl-spec 0 2  ; domain boundaries
                             um/tent-fn
                             [2 2] ; parameters for curve fn
                             1 ; num compositions
                             [] 2 ; initial x's and number of steps
                             :fixedpt-x 1.0))

)
