(ns pbranes.canvas.graph-tools)

(defn get-chart-dimension 
  "Returns one dimension of the chart graph.
   Pass in true for the height and false for the width"
  ([mc] (get-chart-dimension mc 0 false))
  ([{:keys [canvas]} margin isHeight?]
   (when canvas
     (if isHeight?
       (-> canvas (.-height) (- (* 2 margin)))
       (-> canvas (.-width) (- (* 2 margin)))))))

(defn get-spacing [length n]
  (/ length n))

(defn calc-spacing
  "Returns the spacing based on the chart dimensions.
   Calculates both the x and y spacing but returns the smaller
   of the two."
  [mc vert-lines horiz-lines margin]
  (let [chart-width (get-chart-dimension mc margin false)
        chart-height (get-chart-dimension mc margin true)
        x-spacing (get-spacing chart-width vert-lines)
        y-spacing (get-spacing chart-height horiz-lines)]
    (min x-spacing y-spacing)))