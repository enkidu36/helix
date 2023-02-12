(ns pbranes.webgl.gl-attribs)

(defn ARRAY-BUFFER [gl]
  ^byte (.-ARRAY_BUFFER gl))

(defn COLOR-BUFFER-BIT [gl]
  ^byte (.-COLOR-BUFFER-BIT gl))

(defn COMPILE-STATUS [gl]
  ^byte (.-COMPILE_STATUS gl))

(defn FLOAT [gl]
  ^byte (.-FLOAT gl))

(defn VERTEX-SHADER [gl]
  ^byte (.-VERTEX_SHADER gl))

(defn FRAGMENT-SHADER [gl]
  ^byte (.-FRAGMENT_SHADER gl))

(defn LINK-STATUS [gl]
  ^byte (.-LINK_STATUS gl))

(defn DEPTH-TEST [gl]
  ^byte (.-DEPTH_TEST gl))

(defn LEQUAL [gl]
  ^byte (.-LEQUAL gl))

(defn DEPTH-BUFFER-BIT [gl]
  ^byte (.-DEPTH_BUFFER_BIT gl))

(defn STATIC-DRAW [gl]
  ^byte (.-STATIC_DRAW gl))

(defn TRIANGLE-STRIP [gl]
  ^byte (.-TRIANGLE_STRIP gl))