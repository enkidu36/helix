(ns pbranes.components.canvas
  (:require [helix.core :refer [defnc]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

(defnc canvas [{:keys [width height]}]

  (let [canvas-ref (hooks/use-ref nil)]
    (hooks/use-effect
     :always
     (let [context (.. canvas-ref -current (getContext "2d"))]
       (js/console.log context)))

    (d/div
     (d/canvas {:id "canvas"
                :ref canvas-ref
                :style {:height height :width width :background-color "blue"}}))))
