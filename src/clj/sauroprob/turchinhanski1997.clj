

;; Second term in first equation in equation (1) in Turchin and Hanski 1997:
;; $\frac{GN^2}{N^2 + H^2}$
(defn turchin-hanski-generalist
  "Second term in first equation in equation (1) in Turchin and Hanski 1997."
  [G H N]
  (let [nsq (* N N)
        hsq (* H H)]
    (double
      (/ (* G nsq)
         (+ nsq hsq)))))

(def thg turchin-hanski-generalist)

;; It appears that when N = H, the value of the expression is always half
;; of G, which is what the authors' remark near the end of p. 845 suggests:
(thg 5000 700 700)

;; Well take a way the *G*.  Then we have $\frac{N^2}{N^2 + H^2}$

;; Forget about the squares for the moment.  When $x = y$
;; so 
;; $x/(x+y) = x/(2x) = 1/2$
;; Then $G\frac{x}{x+y} = \frac{G}{2}$
