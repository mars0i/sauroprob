(ns utils.json
  (:require ;[clojure.data.json :as cljson]
            [jsonista.core :as nista]))


;(println (json/write-str vl-spec3))
;(with-out-str (json/pprint vl-spec3))

(defn edn2json-file
  "Convert edn to json and pretty-print it to file."
  [filename edn]
  (with-open [file (clojure.java.io/writer filename)]
    (nista/write-value file edn (nista/object-mapper {:pretty true}))))

(defn edn2json-string
  "Convert edn to json and return as a pretty-printed string."
  [edn]
  (nista/write-value-as-string edn
                     (nista/object-mapper {:pretty true})))
