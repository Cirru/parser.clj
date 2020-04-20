
(ns cirru-parser.updater (:require [respo.cursor :refer [update-states]]))

(defn updater [store op op-data op-id op-time]
  (case op
    :states (update-states store op-data)
    :content (assoc store :content op-data)
    :hydrate-storage op-data
    store))
