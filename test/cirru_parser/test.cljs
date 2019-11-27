(ns cirru-parser.test
  (:require [cljs.test :refer [deftest run-tests is testing]]
            [cirru-parser.core :refer [parse pare]]
            [cljs.reader :refer [read-string]]
            ["fs" :as fs]))

(defn slurp [x] (fs/readFileSync x "utf8"))

; (deftest pare-test
;   (testing "pare lines"
;     (is (= (pare "as aa () \n  a" "") []))))

(defn parse-file [x]
  (let
    [file (str "data/cirru/" x ".cirru")]
    (pare (slurp file) file)))

(defn parse-edn [x]
  (let
    [file (str "data/ast/" x ".edn")]
    (read-string (slurp file))))

(defn check [x]
  ; (println (parse-file x))
  ; (println (parse-edn x))
  (=
    (parse-file x)
    (parse-edn x)))

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
