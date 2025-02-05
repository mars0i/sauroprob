(ns sauroprob.yo
  (:require [scicloj.kindly.v4.kind :as kind]
            [scicloj.clay.v2.api :as clay]
            ;[aerial.hanami.common :as hc]
            ;[aerial.hanami.templates :as ht]
            [utils.math :as um]
            [sauroprob.hanami :as sh]))

;; Lotta handy stuff below is from here:
;; https://scicloj.github.io/clay/#api
;;
;; Looks like this lets you set some kind of global config:
;; https://scicloj.github.io/clay/#configuration

(comment
  (clay/browse!) ; make Clay open the browser window

  ;; opens on localhost:1971
  (clay/make! {:source-path "src/clj/sauroprob/yo.clj"
               :live-reload true}) ; doesn't work consistently

  ;; This is what I'm talking about!  One plot by itself.
  (clay/make! {:single-value 
               (kind/vega-lite 
                 (sh/make-vl-spec 0.0 3.0 um/normalized-ricker
                                  [2.95] [1 3 5] [] 1 :fixedpt-x 1.0))
               :hide-ui-header true
               :hide-info-line true})

  ;; Here's another way to do it.  Note the quote:
  (clay/make! {:single-form 
               '(kind/vega-lite 
                 (sh/make-vl-spec 0.0 10.0 um/normalized-ricker
                                  [4.95] [40] [] 1 :fixedpt-x 1.0))
               :hide-ui-header true
               :hide-info-line true})

)

;; By default, a Clay notebook shows both the code and the result
;; of an evaluated form. Here are a few ways one may hide the code:
;;
;;    Add the metadata :kindly/hide-code true to the form (e.g.,
;;    by preceding it with ^:kindly/hide-code).
;;
;;    Add the metadata :kindly/hide-code true to the value.
;;
;;    Globally define certain kinds (e.g., :kind/md, :kind/hiccup)
;;    to always hide code (on project level or namespace level) by
;;    adding theme as a set to the project config or namespace config,
;;    e.g., :kindly/options {:kinds-that-hide-code #{:kind/md
;;    :kind/hiccup}}.


(kind/vega-lite (sh/histogram 200
                              (take 10000
                                    (iterate (um/normalized-ricker 2.5) 0.8))))

(kind/vega-lite (sh/histogram 100 (take 10000 (iterate (um/logistic 4.0) 0.6))))

(kind/vega-lite 
  (sh/make-vl-spec 0.0 3.0 um/normalized-ricker
                   [2.95] [1 2 4 8] [] 1 :fixedpt-x 1.0))

(kind/vega-lite
  (sh/vl-plot-seq "normal"
                  (take 100 (iterate (um/normalized-ricker 3.5) 0.1))))
