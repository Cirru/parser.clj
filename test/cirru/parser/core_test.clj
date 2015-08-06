(ns cirru.parser.core-test
  (:require [clojure.test :refer :all]
            [cheshire.core :refer :all]
            [cirru.parser.core :refer :all]))

; (deftest pare-test
;   (testing "pare lines"
;     (is (= (pare "as aa () \n  a" "") []))))

(defn parse-file [x]
  (let
    [ file (str "cirru/" x ".cirru")]
    (pare (slurp file) file)))

(defn parse-json [x]
  (let
    [ file (str "ast/" x ".json")]
    (into [] (parse-string (slurp file)))))

(defn check [x]
  ; (println (parse-file x))
  ; (println (parse-json x))
  (=
    (parse-file x)
    (parse-json x)))

(deftest pare-comma
  (testing "pare comma"
    (is (check "comma"))))

(deftest pare-demo
  (testing "pare demo"
    (is (check "demo"))))

(deftest pare-folding
  (testing "pare folding"
    (is (check "folding"))))

(deftest pare-html
  (testing "pare html"
    (is (check "html"))))

(deftest pare-indent
  (testing "pare indent"
    (is (check "indent"))))

(deftest pare-line
  (testing "pare line"
    (is (check "line"))))

(deftest pare-parentheses
  (testing "pare parentheses"
    (is (check "parentheses"))))

(deftest pare-quote
  (testing "pare quote"
    (is (check "quote"))))

(deftest pare-spaces
  (testing "pare spaces"
    (is (check "spaces"))))

(deftest pare-unfolding
  (testing "pare unfolding"
    (is (check "unfolding"))))
