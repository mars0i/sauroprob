;; See https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/Display.20multiple.20kind.20elements.20inside.20a.20let.20or.20a.20function.3F/near/504472532<D-b>

(let [_ nil]
  (kind/fragment
    [(kind/md "Before")
     (kind/md "---")
     (kind/md "After")]))

(let [plot1 (plotly/layer-line 
              (tc/dataset {:x [0 2], :y [0 2], :fun "y=x"})
              {:=x :x, :=y, :y})
      plot2 (plotly/layer-line 
              (tc/dataset {:x [0 2], :y [2 0], :fun "y=-x"})
              {:=x :x, :=y, :y})]
  (kind/fragment [plot1 plot2])
  ;; Or:
  ;(kind/hiccup (into [:div {:style "grid"}] [plot2 plot1]))
  )



