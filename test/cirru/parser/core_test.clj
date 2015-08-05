(ns cirru.parser.core-test
  (:require [clojure.test :refer :all]
            [cirru.parser.core :refer :all]))

(deftest simple-text
  (testing "simple"
    (is (= (simple 1) 3))))
