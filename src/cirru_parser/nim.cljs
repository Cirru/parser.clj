
(ns cirru-parser.nim
  (:require ["@cirru/parser.nim" :refer [parseCirru]] [clojure.string :as string]))

(defn merge-chars [xs]
  (if (nil? xs)
    ""
    (let [*result (atom "")]
      (.forEach xs (fn [x] (swap! *result str (js/String.fromCharCode x))))
      @*result)))

(defn transform-data [tree]
  (if (= (.-kind tree) 1)
    (let [expr-list (.-list tree)]
      (if (nil? expr-list)
        []
        (let [*result (atom [])]
          (.forEach expr-list (fn [child] (swap! *result conj (transform-data child))))
          @*result)))
    (merge-chars (.-text tree))))

(defn parse [code]
  (let [js-tree (parseCirru (-> code (.split "") (.map (fn [c] (.charCodeAt c 0)))))]
    (transform-data js-tree)))
