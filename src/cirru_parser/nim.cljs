
(ns cirru-parser.nim
  (:require ["@cirru/parser.nim" :refer [parseCirru]] [clojure.string :as string]))

(defn transform-data [tree]
  (if (= (.-kind tree) 1)
    (let [expr-list (-> tree .-list)]
      (if (nil? expr-list) (js/Array.) (-> expr-list (.map transform-data) (js->clj))))
    (-> tree .-text (.map (fn [n] (js/String.fromCharCode n))) (.join ""))))

(defn parse [code]
  (let [js-tree (parseCirru (-> code (.split "") (.map (fn [c] (.charCodeAt c 0)))))]
    (transform-data js-tree)))
