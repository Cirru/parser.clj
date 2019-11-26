
(ns cirru-parser.main
 (:require [cirru-parser.core :as parser]
           ["fs" :as fs]
           ["path" :as path]))

(defn parse-file []
 (let [started-time (.now js/Date)
       content (fs/readFileSync (path/join js/process.env.PWD (aget js/process.argv 2)) "utf8")]
  (println (type (parser/pare content nil)))
  (println "Cost" (- (.now js/Date) started-time))))

(defn main! []
 (parse-file))

(defn reload! []
 (parse-file))
