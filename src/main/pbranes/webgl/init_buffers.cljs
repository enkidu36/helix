(ns pbranes.webgl.init-buffers)

(defn init-position-buffer [gl]
  ;; Create a buffer for the square's positions.
  ;; Create a Float32Array to fill the current buffer.  
  (let [position-buffer (.createBuffer gl)
    positions (js/Float32Array. [1.0 1.0 -1.0 1.0 1.0 -1.0 -1.0 -1.0])]
    ;; Select the positionBuffer as the one to apply buffer
    ;; operations to from here out.
    (.bindBuffer gl (. gl -ARRAY_BUFFER) position-buffer)
        
    ;; Now pass the list of positions into WebGL to build the 
    ;; shape.
    (.bufferData gl (. gl -ARRAY_BUFFER) positions (. gl -STATIC_DRAW))
    position-buffer))

(defn init-buffers [gl]
  (clj->js {:position (init-position-buffer gl)}))
