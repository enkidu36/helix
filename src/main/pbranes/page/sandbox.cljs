(ns pbranes.page.sandbox
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [pbranes.component.canvas :refer [canvas]]))

(defnc sandbox []
  (<>
   ($ canvas {:width 400 :height 400})
   (d/div "hello")))