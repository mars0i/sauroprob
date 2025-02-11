;; Experiments to go with Devaney 3rd ed
^:kindly/hide-code
(ns sauroprob.devaney
  (:require [clojure.math :as m]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.clay.v2.api :as clay] ; needed for clay eval keymappings
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.hanami :as sh]))

(kind/vega-lite (sh/make-vl-spec -4.0 0.5 um/scaled-exp [1] [1] [] 1 :y-lims [0.0 2.0]))
(kind/vega-lite (sh/make-vl-spec -4.0 2.0 um/scaled-exp [(/ m/E)] [1] [] 1 :y-lims [0.0 2.0]))
(kind/vega-lite (sh/make-vl-spec -4.0 2.0 um/scaled-exp [(/ m/E)] [1] [-0.5 1.70] 2 :y-lims [0.0 2.0]))
(kind/vega-lite (sh/make-vl-spec -4.0 2.0 um/scaled-exp [(- (/ m/E) 0.1)] [1] [] 1 :y-lims [0.0 2.0]))
(kind/vega-lite (sh/make-vl-spec -4.0 2.0 um/scaled-exp [(- (/ m/E) 0.1)] [1] [1.85] 6 :y-lims [0.0 2.0]))
(kind/vega-lite (sh/make-vl-spec -2.0 0.5 um/scaled-exp [(- m/E)] [1] [-0.5 -1.5] 5 :y-lims [-2.0 0.0]))

^:kindly/hide-code
(comment
  (require 'clojure.repl)
  (clojure.repl/pst)
)
