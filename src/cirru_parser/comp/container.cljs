
(ns cirru-parser.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp defeffect cursor-> <> div button textarea span input]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [cirru-parser.config :refer [dev?]]))

(defcomp
 comp-container
 (reel)
 (let [store (:store reel), states (:states store)]
   (div
    {:style (merge ui/global ui/fullscreen ui/column)}
    (div
     {:style (merge ui/row-parted {:padding 8})}
     (span nil)
     (button {:inner-text "Parse", :style ui/button}))
    (div
     {:style (merge ui/expand ui/row)}
     (textarea
      {:style (merge ui/expand ui/textarea {:font-family ui/font-code}),
       :placeholder "Text..."})
     (textarea
      {:style (merge ui/expand ui/textarea {:font-family ui/font-code}),
       :placeholder "Result..."}))
    (when dev? (cursor-> :reel comp-reel states reel {})))))
