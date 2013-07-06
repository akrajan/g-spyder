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

(def-driver-fn do-crawl [driver]
   (let [deal-permalinks (get-deal-listing driver "http://www.groupon.com/browse/seattle?category=home-and-auto")
         ;; deal-permalinks (take 1 deal-permalinks)
         deals-futures (map (fn [deal-url number]
                              (log-as (str number ". " deal-url)
                                      (future (get-deal deal-url))))
                            deal-permalinks
                            (range))
         deal-info (doall (map deref deals-futures))]
     (process deal-info)))


