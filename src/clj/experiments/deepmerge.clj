
;; Re https://clojurians.zulipchat.com/#narrow/channel/422115-clay-dev/topic/Plotly.20line.20attributes

(kindly/deep-merge {:data {0 {:line {:width 1}}}}
                   {:data {0 {:line {:dash "dot"}}}})
;=> {:data {0 {:line {:width 1, :dash "dot"}}}}

(kindly/deep-merge {:data [{:line {:width 1}}]}
                   {:data [{:line {:dash "dot"}}]})
;=> {:data [{:line {:dash "dot"}}]}

(kindly/deep-merge {:data [:blah :ble]}
                   {:data [:zo :ze]})

(map? [1 2 3])

(merge-with + {:a 1 :b 2} {:b 14 :a 23})

;(merge-with + [1 2] [23 14])

;(into (sorted-map) [1 2 3 4])

(defn vec2map
  [v]
  (into {} (map #(vector %1 %2) (range) v)))

(def vmap (vec2map [:a :b :c :d]))

(defn map2vec
  [m]
  (vec (vals m)))

(map2vec vmap)

(defn mapv*
  "Like mapv, returns a vector that results from applying f to the elements
  of supplied vectors, but if one vector is longer than the other, its
  elements are added to the end of the resulting vector.  Because of this
  behavior, mapv* is limited to exactly two vector arguments."
  [f v0 v1]
  (let [v0-len (count v0)
        v1-len (count v1)
        extra (cond (> v0-len v1-len) (drop v1-len v0)
                    (> v1-len v0-len) (drop v0-len v1)
                    :else [])
        combined (mapv f v0 v1)]
    (into combined extra)))

(defn vec2map
  [v]
  (zipmap (range) v))

(mapv* + [1 2 3 4] [5 6 7 8 9 10])
(mapv* + [5 6 7 8 9 10] [1 2 3 4])

;; This shows what's problematic about the above definition:
(mapv* / [1 2 3 4] [5 6 7 8 9 10])
;; Then again, merge-with has the same problem.

(kindly/deep-merge {:a 1 :b 2} [3 4 5])
(kindly/deep-merge [3 4 5] {:a 1 :b 2})

(empty? [])
(empty? '())
(empty? nil)
(nil? [])
(nil? '())
(nil? nil)

(type [])
(type {})

(defn deep-merge2
  "Recursively merges maps and vectors."
  [& xs]
  (let [first-coll (first xs)
        init-output (if (vector? first-coll) [] {})]
    (->> xs
         (remove nil?)
         (reduce (fn m [a b]
                   (cond (and (map? a) (map? b)) (merge-with m a b)
                         (and (vector? a) (vector? b)) (mapv* m a b)
                         :else b))
                 init-output))))

(deep-merge2 {:data {0 {:line {:width 1}}}}
             {:data {0 {:line {:dash "dot"}}}})
;=> {:data {0 {:line {:width 1, :dash "dot"}}}}

(deep-merge2 {:data [{:line {:width 1}}]}
             {:data [{:line {:dash "dot"}}]})
;=> {:data [{:line {:dash "dot"}}]}

(deep-merge2 {:data [:blah :ble]}
             {:data [:zo :ze]})

