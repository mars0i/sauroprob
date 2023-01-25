(ns sauroprob.clerkscratch
  (:require [sauroprob.core :refer :all]
            [utils.json :as json]
            [aerial.hanami.common :as hc]
            [aerial.hanami.templates :as ht]
            [nextjournal.clerk :as clerk]))

(comment (clerk/serve! {:browse? true}))
(comment (clerk/show! "src/clj/sauroprob/clerkscratch.clj"))

(def logistic-data
  (mapcat (fn [m]
            (let [mu (round-to m 1)] ; strip float slop created by range
              (vl-fn-ify mu 0.0 1.001 0.01 mu (logistic mu))))
          (range 1.0 4.5 0.1))) ; don't use integers--some will mess up subs

(def map-lines-data
  (mapcat (fn [m]
            (let [mu (round-to m 1)] ; strip float slop created by range
              (vl-iter-lines (logistic mu) mu 0.75 20 "map")))
          (range 1.0 4.5 0.1))) ; don't use integers--some will mess up subs

(def vl-spec4
  (-> (hc/xform ht/layer-chart
                {:LAYER
                 ;; Note because the y=x line is unaffected by sliders, etc., it fixes the 
                 ;; dimensions of the axes; otherwise we'd need to fix that by other means.
                 [(hc/xform ht/line-chart
                            :DATA [{"x" 0, "y" 0, "label" "y=x"} {"x" 1, "y" 1, "label" "y=x"}]
                            :COLOR "label"
                            :SIZE 1.0
                            :WIDTH 400
                            :HEIGHT 400)
                  (hc/xform ht/line-chart 
                            :DATA logistic-data
                            :TRANSFORM [{:filter {:field "f-param" :equal {:expr "MuSliderVal"}}}]
                            :COLOR "label"
                            :WIDTH 400
                            :HEIGHT 400)
                  (-> (hc/xform ht/line-chart 
                            :DATA map-lines-data ;(vl-iter-lines (logistic 2.5) 2.5 0.99 10 "yow") ; TEMP KLUDGE for testing
                            :TRANSFORM [{:filter {:field "f-param" :equal {:expr "MuSliderVal"}}}]
                            :COLOR "label"
                            :MSDASH [1 2] ; dashed [stroke length, space between]
                            :WIDTH 400
                            :HEIGHT 400)
                      (assoc-in [:encoding :order :field] "ord"))
                  ]})
      ;; The "params" key has to be at the top level (if there are layers, outside the layers vector)
      (assoc :params [{:name "MuSliderVal" ; name of slider variable
                       :value 2.5            ; default value
                       :bind {:input "range" ; "range" makes it a slider
                              :min 1.0 :max 4.0 :step 0.1}}]) ; slider config
                              ;; My data goes beyond 4.0, but the setup is not right
                              ;; for more than 4.0, so I'm restricting the slider
                              ;; for now.
      ))

(clerk/vl vl-spec4)

(def vl-spec3
  (-> (hc/xform ht/layer-chart
                {:LAYER
                 ;; Note because the y=x line is unaffected by sliders, etc., it fixes the 
                 ;; dimensions of the axes; otherwise we'd need to fix that by other means.
                 [(hc/xform ht/line-chart
                            :DATA [{"x" 0, "y" 0, "label" "y=x"} {"x" 1, "y" 1, "label" "y=x"}]
                            :COLOR "label"
                            :SIZE 1.0
                            :WIDTH 400
                            :HEIGHT 400)
                  (hc/xform ht/line-chart 
                            :DATA logistic-data
                            :TRANSFORM [{:filter {:field "label" :equal {:expr "MuSliderVal"}}}]
                            :COLOR "label"
                            :WIDTH 400
                            :HEIGHT 400)
                  ]})
      ;; The "params" key has to be at the top level (if there are layers, outside the layers vector)
      (assoc :params [{:name "MuSliderVal" ; name of slider variable
                       :value 2.5            ; default value
                       :bind {:input "range" ; "range" makes it a slider
                              :min 1.0 :max 4.0 :step 0.1}}]) ; slider config
      ))

;; Example of a completely non-working version (because the slider code
;; is not at the top level):
(def vl-spec2bad
  (-> (hc/xform ht/layer-chart
                {:LAYER
                 [(-> 
                    (hc/xform ht/line-chart 
                              :DATA logistic-data
                              :TRANSFORM [{:filter {:field "label" :equal {:expr "MuSliderVal"}}}] ; :equal "mu_slider_val" doesn't work
                              :COLOR "label")
                    ;; The "params" key should be at the same level as "data".
                    (assoc :params [{:name "MuSliderVal" ; name of slider variable
                                     :value 2.5            ; default value
                                     :bind {:input "range" ; "range" makes it a slider
                                            :min 1.0 :max 4.0 :step 0.1}}]))]})))


