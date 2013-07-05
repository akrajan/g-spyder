(ns g-spyder.utils)

(defn insert-second [what coll]
  (map (fn [c]
         (if (coll? c)
           (let [[fn-name & rst] c]
             (list* fn-name what rst))
           c))
    coll))


(defmacro with [subject & statements]
  (let [x `x#
        forms (insert-second x statements)]
    `(let [~x ~subject]
       (do ~@forms))))


(defmacro tap [[binding source] & tap-body]
  `(let [~binding ~source]
     (do ~@tap-body)
     ~binding))


(defmacro as-log [log-str & body]
  `(do
     (println "Starting: " ~log-str)
      (let [res# (do ~@body)]
        (println "Finished: " ~log-str)
        res#)))


(defn arg-count [f]
  (let [m (first (.getDeclaredMethods (class f)))
        p (.getParameterTypes m)]
    (alength p)))


(defn select-keys-in-coll [keys coll]
  (map #(select-keys %1 keys) coll))


(defn unvar [item]
  (if (var? item) @item item))
