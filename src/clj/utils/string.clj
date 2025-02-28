(ns utils.string)

(defn u-sub-char
  "If n is a single-digit integer in [0, ..., 9], returns the
  Unicode supscript character for n.  Otherwise returns nil."
  [n]
  (if (and (integer? n)
           (not (neg? n))
           (>= n 0)
           (<= n 9))
    (char (+ 0x2080 n))
    nil))


;; Unicode supports a limited number of supscript and superscript characters,
;; including single-digit integers.  The superscripts are inconsistent because
;; some are inherited from an earlier character encoding.
(defn u-sup-char
  "If n is a single-digit integer in [0, ..., 9], returns the
  Unicode superscript character for n.  Otherwise returns nil."
  [n]
  (when (and (integer? n) (>= n 0) (< n 10))
    (cond (= n 0) \u2070  ; superscript/subscript block
          (= n 1) \u00B9  ; Latin-1 supplement block
          (= n 2) \u00B2  ; Latin-1 supplement block
          (= n 3) \u00B3  ; Latin-1 supplement block
          (and (>= n 4) (<= n 9)) (char (+ 0x2070 n)) ; superscript/subscript block
          :else nil)))

(defn u-sup-chars
  [n]
  (when (and (integer? n) (>= n 0) (>= n 10))
    (loop [curr-chars [], this-n (rem n 10), next-n (quot n 10)]
      (recur (conj curr-chars (u-sup-char this-n))))))


(comment
  (map #(str "F" (u-sub-char %)) (concat (range 11) [0.2 -3])) ; last 3 s/b empty since nil
  (map #(str "F" (u-sup-char %)) (concat (range 11) [0.2 -3])) ; last 3 s/b empty since nil
)

;;TODO Make string versions of above that take arbitrary positive integers
;; and return strings of superscript or subscript characters.

