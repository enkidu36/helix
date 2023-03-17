(ns pbranes.webgl.init-buffers
  (:require [pbranes.webgl.constants :refer [ARRAY-BUFFER STATIC-DRAW ELEMENT-ARRAY-BUFFER] ]))


;; (def colors (js/Float32Array.
;;              [1.0 1.0 1.0 1.0 ;; white
;;               1.0 0.0 0.0 1.0 ;; red
;;               0.0 1.0 0.0 1.0 ;; green
;;               0.0 0.0 1.0 1.0 ;; blue
;;               ]))

(def positions-2d (js/Float32Array. [1.0 1.0 -1.0 1.0 1.0 -1.0 -1.0 -1.0]))
(def positions
  (js/Float32Array.
   [;; Front face
    -1.0 -1.0 1.0 1.0 -1.0 1.0 1.0 1.0 1.0 -1.0 1.0 1.0
 ;; Back face
    -1.0 -1.0 -1.0 -1.0 1.0 -1.0 1.0 1.0 -1.0 1.0 -1.0 -1.0
 ;; Top face
    -1.0 1.0 -1.0 -1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 -1.0
 ;; Bottom face
    -1.0 -1.0 -1.0 1.0 -1.0 -1.0 1.0 -1.0 1.0 -1.0 -1.0 1.0
 ;; Right face
    1.0 -1.0 -1.0 1.0 1.0 -1.0 1.0 1.0 1.0 1.0 -1.0 1.0
 ;; Left face
    -1.0 -1.0 -1.0 -1.0 -1.0 1.0 -1.0 1.0 1.0 -1.0 1.0 -1.0]))

(def indices
  [0
   1
   2
   0
   2
   3  ;; front
   4
   5
   6
   4
   6
   7  ;; back
   8
   9
   10
   8
   10
   11  ;; top
   12
   13
   14
   12
   14
   15  ;; bottom
   16
   17
   18
   16
   18
   19  ;; right
   20
   21
   22
   20
   22
   23  ;; left
   ])

(defn init-position-buffer [gl]
  ;; Create a buffer for the square's positions.
  ;; Create a Float32Array to fill the current buffer.  
  (let [position-buffer (.createBuffer gl)]
    
    ;; Select the positionBuffer as the one to apply buffer
    ;; operations to from here out.
    (.bindBuffer gl ARRAY-BUFFER position-buffer)

    ;; Now pass the list of positions into WebGL to build the 
    ;; shape.
    (.bufferData gl ARRAY-BUFFER positions STATIC-DRAW)

    position-buffer))

(def face-colors
  [[1.0 1.0 1.0 1.0] ;; Front face: white
   [1.0 0.0 0.0 1.0] ;; Back face: red
   [0.0 1.0 0.0 1.0] ;; Top face: green
   [0.0 0.0 1.0 1.0] ;; Bottom face: blue
   [1.0 1.0 0.0 1.0] ;; Right face: yellow
   [1.0 0.0 1.0 1.0] ;; Left face: purple
   ])

;; Convert face colors into an array of colors for all the verticies for each square
;; "[[white][white][white][white][red][red][red][red][green][green][green][green]...]"
  (def colors (vec (flatten (map #(list % % % %) face-colors))))

  (comment
    (prn (vec (flatten (map #(list % % % %) face-colors))))
    (prn (map #(list % % % %) face-colors))
    (prn (js/Float32Array. colors))
    (js/console.log (clj->js colors)))

  (defn init-color-buffer [gl]
    (let [color-buffer (.createBuffer gl)]
      (.bindBuffer gl ARRAY-BUFFER color-buffer)
      (.bufferData gl ARRAY-BUFFER (js/Float32Array. colors) STATIC-DRAW)

      color-buffer))

(defn init-index-buffer [gl]
  (let [index-buffer (.createBuffer gl)]
    (.bindBuffer gl ELEMENT-ARRAY-BUFFER index-buffer)

    ;; This array defines each face as two triangles, using the 
    ;; indices into the vertex array to specify each triangle;s
    ;; position

    (.bufferData gl ELEMENT-ARRAY-BUFFER (js/Uint16Array. indices) STATIC-DRAW)
    index-buffer))

(defn init-buffers [gl]
  (clj->js {:position (init-position-buffer gl)
            :color (init-color-buffer gl)
            :indices (init-index-buffer gl)}))
