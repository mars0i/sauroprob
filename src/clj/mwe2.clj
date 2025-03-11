(ns mwe2
  (:require [scicloj.kindly.v4.kind :as kind]))

;; The next comment only displays '(kind/html "' and makes later lines invisible:

;; (kind/html "<script type=\"text/javascript\">42</script>")

(kind/html "Won't display.")

(kind/md "Won't display either.")

;; ## Is this heading visible?

;; Is this comment visible?

;; $\text{Adding LaTeX in a comment seems to reset things.}$

;; ## What about this heading?

;; And is this comment visible?

