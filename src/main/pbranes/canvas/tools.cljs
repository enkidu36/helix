(ns pbranes.canvas.tools
  (:require [goog.dom :refer [getElement]]
            [monet.canvas :as canvas]))

(defn get-monet-canvas [id]
  (canvas/init (getElement id) "2d") )