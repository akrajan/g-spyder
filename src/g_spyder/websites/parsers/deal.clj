(ns g-spyder.websites.parsers.deal
  (:use clj-webdriver.taxi)
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

(defn parse-deal [driver]
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

