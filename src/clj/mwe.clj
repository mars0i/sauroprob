(ns mwe
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.clay.v2.api :as clay]))

(defn mapv*
  "Like mapv, returns a vector that results from applying f to the elements
  of supplied vectors, but if one vector is longer than the other, its
  elements are added to the end of the resulting vector.  Because of this
  behavior, mapv* is limited to exactly two vector arguments."
  [f v0 v1]
  (let [v0-cnt (count v0)
        v1-cnt (count v1)
        extra (cond (> v0-cnt v1-cnt) (drop v1-cnt v0)
                    (> v1-cnt v0-cnt) (drop v0-cnt v1)
                    :else [])
        combined (mapv f v0 v1)]
    (into combined extra)))

(defn vec2map
  [v]
  (zipmap (range) v))

;; This shows what's problematic about the above definition:
(mapv* / [1 2 3 4] [5 6 7 8 9 10])
;; Then again, merge-with has the same problem.

(defn deep-merge2
  "Recursively merges maps and vectors."
  [& xs]
  (->> xs
       (remove nil?)
       (reduce (fn m [a b]
                 (cond (and (map? a) (map? b)) (merge-with2 m a b)
                       (and (vector? a) (vector? b)) (mapv* a b)
                       (and (vector? a) (map? b)) (merge-with2 (vec2map a) b)
                       (and (map? a) (vector? b)) (merge-with2 a (vec2map b))))
                   b)
               {}))

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
(def two-plots-bare {:concat [plot1 plot2]})

^:kindly/hide-code
(def two-plots {:concat [plot1 plot2]
                :columns 2,
                :width 400 
                :height 400
                :background "floralwhite"})

^:kindly/hide-code
(def two-plots-horiz {:hconcat [plot1 plot2]
                      :columns 2,
                      :width 400 
                      :height 400
                      :background "floralwhite"})

^:kindly/hide-code
(def two-plots-vert {:vconcat [plot1 plot2]
                     :columns 2,
                     :width 400 
                     :height 400
                     :background "floralwhite"})

^:kindly/hide-code
(def eight-plots {:hconcat [plot1 plot2
                           plot1 plot2
                           plot1 plot2
                           plot1 plot2]
                  :columns 3,
                  ;:rows 2,
                  })

(kind/vega-lite two-plots-bare)
(kind/vega-lite two-plots)
(kind/vega-lite two-plots-horiz)
(kind/vega-lite two-plots-vert)
(kind/vega-lite eight-plots)
(kind/vega-lite plot1)
(kind/vega-lite plot2)

^:kindly/hide-code
(comment
  (require '[oz.core :as oz])
  (oz/view! plot1)
  (oz/view! plot2)
  (oz/view! two-plots)
  (oz/view! eight-plots)
)
