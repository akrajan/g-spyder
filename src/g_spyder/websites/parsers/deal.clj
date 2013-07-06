(ns g-spyder.websites.parsers.deal
  (:use clj-webdriver.taxi
        [g-spyder utils driver-utils])
  (:require [clojure.string :as string]))

(defn get-name [driver]
  (log-as "get-name"
          (css-attribute? driver ".deal_title h2 a" :text)))


(defn get-price [driver]
  (log-as "get-price"
          (xpath-attribute? driver "//span[@itemprop='price']" :text)))

(defn get-subtitle [driver]
  (log-as "get-subtitle"
          (css-attribute? driver ".deal_title .subtitle" :text)))

(defn get-tags [driver]
  (log-as "get-tags"
          (map #(attribute?-trim driver % :text) (css-finder driver ".deal_tags em"))))

(defn get-bought [driver]
  (log-as "get-bought"
          (css-attribute? driver ".bought_message" :text)))

(defn get-company-links [driver]
  (log-as "get-company-links"
          (all-css-attribute? driver "#company_box .company_links a" :href)))

(defn get-company-name [driver]
  (log-as "get-company-name"
          (css-attribute? driver "#company_box .name" :text)))

(defn get-address [driver]
  (log-as "get-address"
          (when-let [addr (css-attribute? driver ".address [itemprop=streetAddress]" :text)]
            (string/replace-first addr "\nGet Directions" ""))))

(defn parse-deal [driver]
  (let [deal-name (get-name driver)
        price (get-price driver)
        subtitle (get-subtitle driver)
        tags (get-tags driver)
        bought-message (get-bought driver)
        company-name (get-company-name driver)
        company-links (get-company-links driver)
        address (get-address driver)]
    {:deal-name deal-name
     :price price
     :subtitle subtitle
     :tags tags
     :bought-message bought-message
     :company-name company-name
     :company-links company-links
     :address address}))

