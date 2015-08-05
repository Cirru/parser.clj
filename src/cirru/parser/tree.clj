
(ns cirru.parser.tree)

(defn append-item [xs level buffer]
  (if (= level 0)
    (conj xs buffer)
    (conj (pop xs)
      (append-item (last xs) (- level 1) buffer))))

(defn create-helper [xs n]
  (if (<= n 1) xs
    [(create-helper xs (- n 1))]))

(defn create-nesting [n]
  (create-helper [] n))
