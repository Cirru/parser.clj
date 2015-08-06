(ns cirru.parser.core-test
  (:require [clojure.test :refer :all]
            [cirru.parser.core :refer :all]))

(deftest pare-test
  (testing "pare lines"
    (is (= (pare "as aa () \n a" "") []))))
