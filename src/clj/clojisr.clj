;; Illustrations of use of the clojisr Clojure->R bridge
;; Based on https://scicloj.github.io/clojisr/clojisr.v1.tutorials.main.html

(ns clojisr.v1.tutorials.main
  (:require [clojisr.v1.r :as r :refer [r eval-r->java r->java java->r
                                        java->clj java->native-clj
                                        clj->java r->clj clj->r ->code
                                        r+ colon require-r]]
            [clojisr.v1.robject :as robject]
            [clojisr.v1.session :as session]
            [tech.v3.dataset :as dataset]
            [tablecloth.api :as tc]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.kindly.v4.api :as kindly]))

(comment


  (r/set-default-session-type! :rserve)
  (r/discard-all-sessions)

  (def x (r "1+2"))
  (class x)
  (r->clj x)
  (class (r->clj x))

  (def y (->> "list(A=1,B=2,'#123strange<text> ()'=3)"
              r
              r->clj))

  (first (:A y))
  (first (:B y))
  (first (nth (vals y) 2))

  (class
  (-> [1 nil 3]
      clj->r)
  )

  (def f (r "function(x) x*10"))
  (class f)
  (f 25)

  (-> "5*5"
      r
      f
      r->clj)

  (r->clj 
    ((r "mean") [1 nil 3] :na.rm true))


  (let [f (r "function(w,x,y=10,z=20) w+x+y+z")]
    (->> [(f 1 2)
          (f 1 2 :y 100)
          (f 1 2 :z 100)]
         (map r->clj)))

  (->> (r+ 1 2 3)
       r->clj)

  (class 
    (r->clj (colon 0 9))
  )

  (let [row-means (r "function(data) rowMeans(data)")] ; define a function from data to rowMeans(data)
    (-> {:x [1 2 3]  ; note these become columns in tech.ml.dataset
         :y [4 5 6]}
        dataset/->dataset
        ; or use:
        ;tc/dataset
        row-means
        r->clj))

  (r "library(dgof)") ; tell the R server to load dgof into the R world

  ;(r "library(dplyr)")

  (let [filter-by-x  (r "function(data) filter(data, x>=2)")   ; define two
        add-z-column (r "function(data) mutate(data, z=x+y)")] ;  clojure functions
    (-> {:x [1 2 3]
         :y [4 5 6]}
        dataset/->dataset
        filter-by-x
        add-z-column
        r->clj))

  ;; Note that you can :refer x.y names either using dot or dash
  (require-r '[stats :refer [ks.test]])
  (require-r '[dgof :refer [ks-test]])
  (r->clj (r.stats/ks-test (range 42) (range 42)))
  (r->clj (r.dgof/ks-test (range 42) (range 42)))



  (-> [1 2 3]
      r.stats/median
      r->clj)

  (-> [1 2 3]
      statz/median
      r->clj)

)
