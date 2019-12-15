
(ns cirru-parser.nim
  (:require ["@cirru/parser.nim" :refer [parseCirru]] [clojure.string :as string]))

(defn transform-data [tree]
  (if (= (.-kind tree) 1)
    (-> tree .-list (.map transform-data) (js->clj))
    (-> tree .-text (.join ""))))

(defn parse [code] (let [js-tree (parseCirru code)] (transform-data js-tree)))
