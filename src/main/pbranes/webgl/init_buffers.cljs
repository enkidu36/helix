(ns pbranes.webgl.init-buffers
  (:require [pbranes.webgl.gl-attribs :refer [ARRAY-BUFFER STATIC-DRAW]]))

(def positions (js/Float32Array. [1.0 1.0 -1.0 1.0 1.0 -1.0 -1.0 -1.0]))

(defn init-position-buffer [gl]
  ;; Create a buffer for the square's positions.
  ;; Create a Float32Array to fill the current buffer.  
  (let [position-buffer (.createBuffer gl)]
    ;; Select the positionBuffer as the one to apply buffer
    ;; operations to from here out.
    (.bindBuffer gl (ARRAY-BUFFER gl) position-buffer)

    ;; Now pass the list of positions into WebGL to build the 
    ;; shape.
    (.bufferData gl (ARRAY-BUFFER gl) positions (STATIC-DRAW gl))
    
    position-buffer))

(def colors (js/Float32Array.
             [1.0 1.0 1.0 1.0 ;; white
              1.0 0.0 0.0 1.0 ;; red
              0.0 1.0 0.0 1.0 ;; green
              0.0 0.0 1.0 1.0 ;; blue
              ]))

(defn init-color-buffer [gl]
  (let [color-buffer (.createBuffer gl)]
    (.bindBuffer gl (ARRAY-BUFFER gl) color-buffer)
    (.bufferData gl (ARRAY-BUFFER gl) colors (STATIC-DRAW gl))
    
    color-buffer))

(defn init-buffers [gl]
  (clj->js {:position (init-position-buffer gl)
            :color (init-color-buffer gl)}))
