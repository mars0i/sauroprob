(module viz.lnvim
  {autoload {a aniseed.core
             str aniseed.string
             nvim aniseed.nvim
             eval conjure.eval
             extract conjure.extract}})

(defn eval-clojure-for-form-viz []
  (eval.eval-str
    {:origin "custom-clay-wrapper"
     :code (str.join
             ""
             ["(scicloj.clay.v2.api/make! {:source-path \"" (nvim.fn.expand "%.") "\" :single-form `" (a.get (extract.form {:root? true}) :content) "})"])}))

(defn eval-clojure-for-ns-viz []
  (eval.eval-str
    {:origin "custom-clay-wrapper"
     :code (str.join
             ""
             ["(scicloj.clay.v2.api/make! {:source-path \"" (nvim.fn.expand "%.") "\"})"])}))

(var clay-not-yet-required true)

(defn on-filetype []
  (when clay-not-yet-required ; no need to run on every new file
    (set clay-not-yet-required false)
    (eval.eval-str 
      {:origin "custom-clay-wrapper"
       :code "(require 'scicloj.clay.v2.api)"})) ; needs to run only once per nvim session
  (nvim.buf_set_keymap
    0 :n "<localleader>ev" ""
    {:callback eval-clojure-for-form-viz})
  (nvim.buf_set_keymap
    0 :n "<localleader>env" ""
    {:callback eval-clojure-for-ns-viz}) )

(def augroup (nvim.create_augroup :viz.lnvim {}))
(nvim.create_autocmd
  :Filetype
  {:group augroup
   :pattern ["clojure"]
   :callback on-filetype})
