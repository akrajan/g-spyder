(ns g-spyder.core
  (:use clj-webdriver.taxi
        [g-spyder driver-utils utils])
  (:require [clojure.string :as string]
            [g-spyder.websites.parsers.deal :as groupon-deal]
            [g-spyder.websites.parsers.deal-listing :as deal-listing]
            [clojure.java.io :as io]))


(def-driver-fn get-deal [driver url]
  (to driver url)
  (groupon-deal/parse-deal driver))

(defn expose-all-deals [driver]
  (when (find-element driver {:css ".pagination .button"})
    (click driver ".pagination .button")
    (recur driver)))

(def-driver-fn get-deal-listing [driver url]
  (->driver driver
            (to url)
            deal-listing/get-deal-urls))

(defn persist-deal-info [filename]
  (log-as "persist-deal-info"
          (fn [deal-info]
            (log-as "persisting deal info"
                    (with-open [w (io/writer filename)]
                      (binding [*out* w]
                        (pr deal-info)))))))


(defn process [deal-info]
  (log-as "process"
          ((persist-deal-info "mytest") deal-info)))

(defn redefine-state []
  (def current-deals (atom ()))
  (def deal-infos (atom ())))

(def-driver-fn crawl-deal-futures [driver permalinks]
  (map (fn [deal-url number]
         (log-as (str number ". " deal-url)
                 (future
                   (tap [current-info (get-deal driver deal-url)]
                        (swap! deal-infos conj current-info)))))
       permalinks
       (range)))

(def-driver-fn do-crawl [driver]
   (let [deal-permalinks (get-deal-listing driver "http://www.groupon.com/browse/seattle?category=home-and-auto")
         ;; deal-permalinks (take 1 deal-permalinks)
         _ (reset! current-deals deal-permalinks)
         deal-browser (new-driver {})
         deals-futures (crawl-deal-futures deal-permalinks)
         deal-info (doall (map deref deals-futures))]
     (.close deal-browser)
     (process deal-info)))


