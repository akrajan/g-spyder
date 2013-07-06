(ns g-spyder.websites.parsers.deal-listing
  (:use clj-webdriver.taxi)
  (:require [clojure.string :as string]))

(defn expose-all-deals [driver]
  (when (find-element driver {:css ".pagination .button"})
    (click driver ".pagination .button")
    (recur driver)))

(defn parse-deal-urls [driver]
  (let [anchors (css-finder driver "#browse-deals .deal-list-tile .deal-permalink")]
    (doall (map #(attribute % :href) anchors))))


(defn get-deal-urls [driver]
  (expose-all-deals driver)
  (parse-deal-urls driver))

