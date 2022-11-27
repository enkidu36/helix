(ns pbranes.canvas.tools
  (:require [goog.dom :refer [getElement]]
            [monet.canvas :as canvas]))

(defn get-monet-canvas [id]
  (canvas/init (getElement id) "2d") )

(defn get-canvas-dimensions [monet-canvas]
  (let [canvas (-> monet-canvas :ctx (.-canvas))]
    {:x (.-width canvas) :y (.-height canvas)}))

(defn get-center [monet-canvas]
  (let [dimensions (get-canvas-dimensions monet-canvas)]
   [(/ (:x dimensions) 2) (/ (:y dimensions) 2)]))


