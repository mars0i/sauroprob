(ns mwe
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.clay.v2.api :as clay]))

^:kindly/hide-code
(def plot1
  {:layer
   [{:encoding
     {:y {:scale {"domain" [0.0 1.0]}, :field "y", :type "quantitative"},
      :x {:scale {"domain" [0.0 1.0]}, :field "x", :type "quantitative"}},
     :mark {:type "line"},
     :data {:values [{"x" 0.0, "y" 0.0}
                     {"x" 1.0, "y" 1.0}]}}]})

^:kindly/hide-code
(def plot2
  {:layer
   [{:encoding
     {:y {:scale {"domain" [0.0 1.0]}, :field "y", :type "quantitative"},
      :x {:scale {"domain" [0.0 1.0]}, :field "x", :type "quantitative"}},
     :mark {:type "line"},
     :data {:values [{"x" 0.0, "y" 1.0}
                     {"x" 1.0, "y" 0.0}]}}]})

^:kindly/hide-code
(def two-plots {:concat [plot1 plot2]
   :width 400 
   :height 400
   :background "floralwhite"})

(kind/vega-lite plot1)
(kind/vega-lite plot2)
(kind/vega-lite two-plots)

^:kindly/hide-code
(comment
  (require '[oz.core :as oz])
  (oz/view! plot1)
  (oz/view! plot2)
  (oz/view! two-plots)
)
