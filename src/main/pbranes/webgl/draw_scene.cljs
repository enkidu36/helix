(ns pbranes.webgl.draw-scene
  (:require [clojure.math :as math]
            [pbranes.webgl.gl-attribs :refer [COLOR-BUFFER-BIT DEPTH-TEST DEPTH-BUFFER-BIT LEQUAL TRIANGLE-STRIP]]
            [gl-matrix :refer [mat4]]))

(set! *warn-on-infer* false)

(defn set-position-attribute [gl buffers program-info]
  (let [num-components 2 ;; pull out 2 values per iteration
        type (. gl -FLOAT) ;; the data in the buffer is 32bit foats
        normalize false ;; don't normalize
        stride 0 ;; how many bites to get from one set of values to the next 0 = use type and num-components
        offset 0
        vertex-pos (.. program-info -attribLocations -vertexPosition)]

    (.bindBuffer gl (. gl -ARRAY_BUFFER) (. buffers -position))
    
    (.vertexAttribPointer gl vertex-pos num-components type normalize stride offset)
    (.enableVertexAttribArray gl vertex-pos)))

(defn draw-scene [gl program-info buffers]
  (.clearColor gl 0.0 0.0 0.0 1.0) ;; Clear to black, fully opaque
  (.clearDepth gl 1.0) ;; Clear everything
  (.enable gl (. gl -DEPTH_TEST)) ;; Enable depth testing
  (.depthFunc gl (LEQUAL gl))

  ;; Clear the canvas before we start drawing on it.
  (.clear gl (bit-or (.-COLOR_BUFFER_BIT gl) (.-DEPTH_BUFFER_BIT gl)))

  ;; Create a perspective matrix, a special matrix that is 
  ;; used to simutalte the distortion of perspective in a camera
  ;; Our field of view is 45 degrees, with a width/height
  ;; ratio that matches the display size of the canvas
  ;; and we only want to see objects between 0.1 units
  ;; and 100 units away from the camera

  (let [field-of-view (/ (* 45 math/PI) 180)
        canvas (. gl -canvas)
        aspect (/ (. canvas -clientWidth) (. canvas -clientHeight))
        z-near 0.1
        z-far 100.0
        projection-matrix (.create mat4)
        model-view-matrix (.create mat4)
        offset 0
        vertex-count 4]

    ;; (js/console.log "consts" field-of-view aspect z-near z-far projection-matrix model-view-matrix)

    ;; note: glmatrix.js always has the first argument
    ;; as the destination to receive the result.
    (.perspective mat4
                  projection-matrix
                  field-of-view aspect
                  z-near 
                  z-far)

    ;; Now move the drawing postion a bit to where we want to 
    ;; start drawing square.
    (.translate mat4
                model-view-matrix ;; destination matrix
                model-view-matrix ;; matrix to translate
                (clj->js [-0.0 0.0 -6.0])) ;; amount to translate

    ;; Tell WebGL how to pull out the positions from the position buffer
    ;; into the vertexPosition attribute
    (set-position-attribute gl buffers program-info)

    ;; Tell WebGL to use our program when drawing
    (.useProgram gl (.-program program-info))

    ;; Set the shader uniforms
    (.uniformMatrix4fv gl (.. program-info -uniformLocations -projectionMatrix) false projection-matrix)
    (.uniformMatrix4fv gl (.. program-info -uniformLocations -modelViewMatrix) false model-view-matrix)

    (.drawArrays gl (TRIANGLE-STRIP gl) offset vertex-count)))



