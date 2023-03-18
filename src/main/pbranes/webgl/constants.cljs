(ns pbranes.webgl.constants
  (:refer-clojure :exclude [byte float int short keep repeat replace]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Clearing Buffers
;; Constants passed to WebGlReneringContext.clear() to clear buffer masks.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def DEPTH-BUFFER-BIT 0x00000100) ;;	Passed to clear to clear the current depth buffer.
(def STENCIL-BUFFER-BIT 0x00000400) ;;	Passed to clear to clear the current stencil buffer.
(def COLOR-BUFFER-BIT 0x00004000) ;;	Passed to clear to clear the current color buffer.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Rendering primitives
;; Constants passed to WebGLRenderingContext.drawElements(). 
;; Or WebGLRenderingContext.drawArrays() to specify what kind of primitive
;; order.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def POINTS 0x0000) ;;	Passed to drawElements or drawArrays to draw single points.
(def LINES 0x0001) ;;	Passed to drawElements or drawArrays to draw lines. Each vertex connects to the one after it.
(def LINE-LOOP 0x0002) ;;	Passed to drawElements or drawArrays to draw lines. Each set of two vertices is treated as a separate line segment.
(def LINE-STRIP 0x0003) ;;	Passed to drawElements or drawArrays to draw a connected group of line segments from the first vertex to the last.
(def TRIANGLES 0x0004) ;;	Passed to drawElements or drawArrays to draw triangles. Each set of three vertices creates a separate triangle.
(def TRIANGLE-STRIP 0x0005) ;;	Passed to drawElements or drawArrays to draw a connected group of triangles.
(def TRIANGLE-FAN 0x0006) ;;	Passed to drawElements or drawArrays to draw a connected group of triangles. Each vertex connects to the previous and the first vertex in the fan.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Getting GL parameter information
;; Constants passed to WebGLRenderingContext.getParameter() to specify what
;; information to return.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def ARRAY-BUFFER 0x8892)
(def ELEMENT-ARRAY-BUFFER 0x8893)
(def LEQUAL 0x0203)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Buffers
;; Constants passed to 
;; WebGLRenderingContext.bufferData (), 
;; WebGLRenderingContext.bufferSubData (), 
;; WebGLRenderingContext.bindBuffer (), or
;; WebGLRenderingContext.getBufferParameter () .
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def STATIC-DRAW 0x88E4)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Data types
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def BYTE 0x1400)
(def UNSIGNED-BYTE 0x1401)
(def SHORT 0x1402)
(def UNSIGNED-SHORT 0x1403)
(def INT 0x1404)
(def UNSIGNED-INT 0x1405)
(def FLOAT 0x1406)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Shaders
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def FRAGMENT-SHADER 0x8B30) ;;	Passed to createShader to define a fragment shader.
(def VERTEX-SHADER 0x8B31) ;; Passed to createShader to define a vertex shader
(def COMPILE-STATUS 0x8B81) ;;	Passed to getShaderParameter to get the status of the compilation. Returns false if the shader was not compiled. You can then query getShaderInfoLog to find the exact error
(def DELETE-STATUS 0x8B80) ;;	Passed to getShaderParameter to determine if a shader was deleted via deleteShader. Returns true if it was, false otherwise.
(def LINK-STATUS 0x8B82) ;;	Passed to getProgramParameter after calling linkProgram to determine if a program was linked correctly. Returns false if there were errors. Use getProgramInfoLog to find the exact error.
(def VALIDATE-STATUS 0x8B83) ;;	Passed to getProgramParameter after calling validateProgram to determine if it is valid. Returns false if errors were found.
(def ATTACHED-SHADERS 0x8B85) ;;	Passed to getProgramParameter after calling attachShader to determine if the shader was attached correctly. Returns false if errors occurred.
(def ACTIVE-ATTRIBUTES 0x8B89) ;;	Passed to getProgramParameter to get the number of attributes active in a program.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Enabliing and Disabling
;; Constants passed to WebGLRenderingContext.enable()
;; or WebGLRenderingContext.disable()
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def DEPTH-TEST 0x0B71) ;;	Passed to depthFunction or stencilFunction to specify depth or stencil tests will pass if the new depth value is less than or equal to the stored value.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Textures
;; Constants passed to WebGLRenderingContext.texParameteri (), 
;; WebGLRenderingContext.texParameterf (), 
;; WebGLRenderingContext.bindTexture (),
;; WebGLRenderingContext.texImage2D (), and 
;; others.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def TEXTURE-2D	0x0DE1) ;;	Passed to enable/disable to turn on/off the depth test. Can also be used with getParameter to query the depth test.
