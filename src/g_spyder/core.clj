(ns g-spyder.core
  (:use clj-webdriver.taxi
        g-spyder.driver-utils)
  (:require [clojure.string :as string]))

(defn open-all-items []
  (when (find-element {:css ".pagination .button"})
    (click ".pagination .button")
    (recur)))

(defn get-name [driver]
  (string/trim (attribute driver ".deal_title h2 a" :text)))

(defn get-price [driver]
  (when-let [span (first (xpath-finder driver "//span[@itemprop='price']"))]
    (string/trim (attribute span :text))))

(defn get-subtitle [driver]
  (string/trim (attribute driver ".deal_title .subtitle" :text)))

(def-driver-fn get-deal [driver url]
  (to driver url)
  (let [name (get-name driver)
        price (get-price driver)
        subtitle (get-subtitle driver)]
    {:name name
     :price price
     :subtitle subtitle}))

(defn get-deal-urls [driver]
  (let [anchors (css-finder driver "#browse-deals .deal-list-tile .deal-permalink")]
    (doall (map #(attribute % :href) anchors))))


(def-driver-fn get-deal-listing [driver url]
  (to driver url)
  (get-deal-urls driver))


(def-driver-fn do-crawl [driver]
   (let [deal-permalinks (get-deal-listing driver "http://www.groupon.com/browse/seattle?category=home-and-auto")
         deal-permalinks (take 1 deal-permalinks)
         deals-futures (map #(future (get-deal %)) deal-permalinks)
         deal-info (map deref deals-futures)]
     deal-info))

