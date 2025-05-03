(ns utils.misc)

(defmacro multidef
  "bindings should be a list of alternating variable names and expressions.
  The expressions will be evaluated to define the variables using `def`.
  This can be used to quickly turn a long `let` binding list into a series
  of `def`s, after removing the body of the let.
  [bindings]
  `(do
     ~@(map (fn [[avar aval]] `(def ~avar ~aval))
            (partition 2 bindings))))

(comment
  (macroexpand-1 '
  (multidef [h (cons 'a [2 3])
             j (* 6 7)])
  )
)

;; For something fancier, possibly resort to using Nathan Marz's Specter. (Hanami uses it, btw.)
(defn multi-assoc-in
  "Like assoc-in, but replaces values of multiple terminal keys of equal
  depth with the same new value. Specifically, performs assoc-in to the map
  m and the result of previous applications of assoc-in. The key sequences
  for subsequent applications of assoc-in are constructed by conj'ing each
  element of final-keys to initial-keys. The replacement value v is used in
  each application of assoc-in."
  [m initial-keys final-keys v]
  (let [key-seqs (map (fn [k] (conj (vec initial-keys) k))
                      final-keys)]
    (reduce (fn [m' ks] (assoc-in m' ks v))
            m key-seqs)))

(comment 
  (def mymap {:a {:b 1
                  :c {:d 2 :e 3}
                  :f {:z 25 :e 54}}
              :g {:h 5
                  :c {:z 7}
                  :l 8}
              :m 9})
  (multi-assoc-in mymap [:a :c] [:e :z] 101)
  (multi-assoc-in mymap [:a :f] [:e :z] 101)
  ;; Works with vector treated as a map:
  (def map-with-vec {:a [{:b 1
                          :c {:d 2 :e 3}},
                         {:h 5
                          :c {:z 7}
                          :l 8}]
                     :m 9})
  (multi-assoc-in map-with-vec [:a 0 :c] [:e :z] 101)
  (multi-assoc-in map-with-vec [:a 1 :c] [:e :z] 101)
)

;; By John Collins at https://stackoverflow.com/a/68476365/1455243
(defn irange
  "Inclusive range function: end element is included."
  ([start end step]
   (take-while (if (pos? step) #(<= % end) #(>= % end)) (iterate #(+ % step) start)))
  ([start end]
   (irange start end 1))
  ([end]
   (irange 0 end))
  ([] (range)))

;; Can be used with partial to produce the composition of f with itself,
;; n times. Applying the resulting function is only very slightly slower
;; than running with all three arguments.
(defn n-comp
  "Applies f to x, and then applies f to the result, performing n
  applications of f. Like 'iterate', but without constructing a lazy
  sequence of intermediate values.  With two arguments, uses 'partial'
  to produce a function that performs the same iteration.  If n <= 1,
  simply returns f given two arguments, or (f x) given three."
  ([f n] 
   (if (<= n 1)
     f
     (partial n-comp f n)))
  ([f n x]
   (if (<= n 1)
     (f x)
     (loop [i n, y x]
       (if (pos? i)
         (recur (dec i), (f y))
         y)))))

  (comment 
  ;; Alternate version of n-comp:

  ;; Blows stack beyond about 19K iterations.
  (defn n-comp-bad
    [f n]
    (apply comp (repeat n f)))

  ;; Doesn't blow the stack, but incredibly slow.
  (defn n-comp-slow
    [f n x]
    (last (take n (iterate f x))))
)

