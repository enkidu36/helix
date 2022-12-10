(ns pbranes.canvas.graph-tools)


(defn get-chart-dimension [{:keys [canvas]} margin isHeight?]
  (js/console.log (.-height canvas))
   (when canvas
     (if isHeight?
       (-> canvas (.-height) (- (* 2 margin)))
       (-> canvas (.-width) (- (* 2 margin))))))

(defn get-spacing [length n]
  (/ length n))