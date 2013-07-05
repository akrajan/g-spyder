(ns g-spyder.core
  (:use clj-webdriver.taxi
        g-spyder.driver-utils)
  (:require [clojure.string :as string]))


(defn get-name [driver]
  (string/trim (attribute driver ".deal_title h2 a" :text)))

(defn get-price [driver]
  (when-let [span (first (xpath-finder driver "//span[@itemprop='price']"))]
    (string/trim (attribute span :text))))

(defn get-subtitle [driver]
  (string/trim (attribute driver ".deal_title .subtitle" :text)))

(defn get-tags [driver]
  (map #(string/trim (attribute driver % :text)) (css-finder driver ".deal_tags em")))

(defn get-bought [driver]
  (string/trim (attribute driver ".bought_message" :text)))

(defn get-company-links [driver]
  (map #(string/trim (attribute driver % :href)) (css-finder driver "#company_box .company_links a")))

(defn get-company-name [driver]
  (string/trim (attribute driver "#company_box .name" :text)))

(defn get-deal-urls [driver]
  (let [anchors (css-finder driver "#browse-deals .deal-list-tile .deal-permalink")]
    (doall (map #(attribute % :href) anchors))))

(def-driver-fn get-deal [driver url]
  (to driver url)
  (let [deal-name (get-name driver)
        price (get-price driver)
        subtitle (get-subtitle driver)
        tags (get-tags driver)
        bought-message (get-bought driver)
        company-name (get-company-name driver)
        company-links (get-company-links driver)]
    {:deal-name deal-name
     :price price
     :subtitle subtitle
     :tags tags
     :bought-message bought-message
     :company-name company-name
     :company-links company-links}))

(defn expose-all-deals [driver]
  (when (find-element driver {:css ".pagination .button"})
    (click driver ".pagination .button")
    (recur driver)))

(def-driver-fn get-deal-listing [driver url]
  (->driver driver
            (to url)
            expose-all-deals
            get-deal-urls))


(def-driver-fn do-crawl [driver]
   (let [deal-permalinks (get-deal-listing driver "http://www.groupon.com/browse/seattle?category=home-and-auto")
         deal-permalinks (take 1 deal-permalinks)
         deals-futures (map #(future (get-deal %)) deal-permalinks)
         deal-info (map deref deals-futures)]
     deal-info))
