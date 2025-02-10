;; Experiments to go with Devaney 3rd ed
^:kindly/hide-code
(ns sauroprob.devaney
  (:require [clojure.math :as m]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.clay.v2.api :as clay] ; needed for clay eval keymappings
            [utils.misc :as msc]
            [utils.math :as um]
            [sauroprob.hanami :as sh]))

(kind/vega-lite (sh/make-vl-spec 0.0 2.0 um/scaled-exp [1] [1] [] 5))

^:kindly/hide-code
(comment
  (require 'clojure.repl)
  (clojure.repl/pst)
)
