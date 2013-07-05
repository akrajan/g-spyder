(ns g-spyder.driver-utils
  (:use clj-webdriver.taxi
        g-spyder.utils))


(defn run-with-new-driver [action]
  (let [driver (new-driver {})]
    (tap [res (action driver)]
         (close driver))))


(defmacro with-new-driver [[name] & body]
  `(run-with-new-driver
    (fn [~name]
      ~@body)))
