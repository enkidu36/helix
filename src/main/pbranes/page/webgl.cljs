(ns pbranes.page.webgl
  (:require [helix.core :refer [defnc <>]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [pbranes.webgl.init-buffers :refer [init-buffers]]
            [pbranes.webgl.draw-scene :refer [draw-scene]]))

(def vs-source
  "
    attribute vec4 aVertexPosition;
    attribute vec4 aVertexColor;
   
    uniform mat4 uModelViewMatrix;
    uniform mat4 uProjectionMatrix;
   
    varying lowp vec4 vColor;
   
    void main() {
      gl_Position = uProjectionMatrix * uModelViewMatrix * aVertexPosition;
      vColor = aVertexColor;
    }
  ")

(def fs-source
  "
    varying lowp vec4 vColor;
   
    void main() {
      gl_FragColor = vColor;
    }
  ")


(defn load-shader
  ;; creates a shader of type, uploads the source and compiles it
  [gl type source]
  (let [shader (.createShader gl type)]

    ; Send the source to the shader object
    (.shaderSource gl shader source)

    ; Compile the shader program
    (.compileShader gl shader)

    ; See if it compiled successfully
    (if (not (.getShaderParameter gl shader (.-COMPILE_STATUS gl)))
      (do
        (js/alert (str "An erro occurred compiling the shaders:" (.getShaderInfoLog gl shader)))
        (.deleteShader shader))
      shader)))

(defn init-shader-program [gl vs-source fs-source]
  (let [vs (load-shader gl (. gl -VERTEX_SHADER) vs-source)
        fs (load-shader gl (. gl -FRAGMENT_SHADER) fs-source)
        shader-program (. gl createProgram)]

    (.attachShader gl shader-program vs)
    (.attachShader gl shader-program fs)
    (.linkProgram gl shader-program)

    (if (not (.getProgramParameter gl shader-program (.-LINK_STATUS gl)))
      (js/alert (str "Unable to initialize the shader program " (.getProgramInfoLog shader-program)))
      shader-program)))

;; Collect all the info needed to use the shader program.
;; Look up which attributes or shader program is using
;; for aVertexPosition, aVertexColor and also
;; look up uniform locations.
(defn program-info [gl shader-program]
  (clj->js {:program shader-program

            :attribLocations {:vertexPosition (.getAttribLocation gl shader-program "aVertexPosition")
                              :vertexColor (.getAttribLocation gl shader-program "aVertexColor")}

            :uniformLocations {:projectionMatrix (.getUniformLocation gl shader-program "uProjectionMatrix")
                               :modelViewMatrix (.getUniformLocation gl shader-program "uModelViewMatrix")}}))

(def square-rotation (atom 0.0))
(def delta-time (atom 0))

(defn main [gl]

  (when (not gl)
    (js/alert "Unable to initialize WebGL. Your browser or machine may not support it."))

  (.clearColor gl 0.0 0.0 0.0 0.0)
  (.clear gl (.-COLOR_BUFFER_BIT gl))

  (let [shader-program (init-shader-program gl vs-source fs-source)
        buffers (init-buffers gl)
        program-info (program-info gl shader-program)
        then (atom 0)
        render (fn render [now]
                 (let [now-s (* now 0.001)]
                   (reset! delta-time (- now-s @then))
                   (reset! then now-s)
                   (draw-scene gl program-info buffers square-rotation)
                   (reset! square-rotation (+ @square-rotation @delta-time))
                   (js/requestAnimationFrame render)))]
    (js/requestAnimationFrame render)))

(defnc webgl-page []
  (let [glcanvas (hooks/use-ref nil)
        paragraph (hooks/use-ref nil)]
    (hooks/use-effect
     []
     :once
     (let [canvas (.-current glcanvas)
           ctx (.getContext canvas "webgl")
           check-txt (if (instance? js/WebGLRenderingContext ctx) "Congrats! You browser suports WebGL" "Failed. You browser does not support WebGL.")]
       (set! (.. paragraph -current -textContent) check-txt)
       (main ctx)))

    (<>
     (d/div {:style {:padding 50 :text-align "center"}}
            (d/p {:ref paragraph} "[ Here would go the results of WebGL feature detection ]"))
     (d/canvas {:ref glcanvas :class "glcanvas"}
               "You browser does not support canvas"))))

