
(ns cirru-parser.tree )

(declare resolve-comma)

(declare comma-helper)

(declare resolve-dollar)

(declare dollar-helper)

(defn resolve-comma [xs] (if (empty? xs) [] (comma-helper [] xs)))

(defn comma-helper [before after]
  (if (empty? after)
    before
    (let [cursor (first after), cursor-rest (subvec after 1)]
      (if (and (vector? cursor) (not (empty? cursor)))
        (let [head (first cursor)]
          (cond
            (vector? head) (comma-helper (conj before (resolve-comma cursor)) cursor-rest)
            (= head ",")
              (comma-helper
               before
               (into [] (concat (resolve-comma (subvec cursor 1)) cursor-rest)))
            :else (comma-helper (conj before (resolve-comma cursor)) cursor-rest)))
        (comma-helper (conj before cursor) cursor-rest)))))

(defn resolve-dollar [xs] (if (empty? xs) [] (dollar-helper [] xs)))

(defn dollar-helper [before after]
  (if (empty? after)
    before
    (let [cursor (first after), cursor-rest (subvec after 1)]
      (cond
        (vector? cursor) (dollar-helper (conj before (resolve-dollar cursor)) cursor-rest)
        (= (first after) "$") (conj before (resolve-dollar cursor-rest))
        :else (dollar-helper (conj before cursor) cursor-rest)))))
