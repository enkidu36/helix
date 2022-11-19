(ns pbranes.page.sandbox
  (:require
   [helix.core :refer [defnc $]]
   [pbranes.components.canvas :refer [canvas]]))

(defnc sandbox []
  ($ canvas {:width 800 :height 800}))