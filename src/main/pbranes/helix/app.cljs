(ns pbranes.helix.app
  (:require [goog.dom :as gdom]
            [helix.core :refer [defnc $]]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]
            ["react-router-dom" :as rr]
            [pbranes.page.home :refer [home]]
            [pbranes.page.math :refer [math]]
            [pbranes.page.cron :refer [cron-page]]
            [pbranes.page.gantt :refer [gantt-page]]))

(defnc layout []
  (d/div {:class "wrapper"}
         (d/header {:class "header"} 
                   (d/nav {:class "nav"}
                          ($ rr/Link {:to "/"} "Home")
                          ($ rr/Link {:to "/math"} "Math Sandbox")
                          ($ rr/Link {:to "/cron"} "Cron Dashboard")
                          ($ rr/Link {:to "/gant"} "Gantt Chart Prototype")))
         (d/div {:class "main"} ($ rr/Outlet))
         (d/footer {:class "footer"} "footer")))

(defnc router []
  ($ rr/Routes
     ($ rr/Route {:path "/" :element ($ layout)}
        ($ rr/Route {:path "/" :element ($ home)})
        ($ rr/Route {:path "/math" :element ($ math)})
        ($ rr/Route {:path "/cron" :element ($ cron-page)})
        ($ rr/Route {:path "/gant" :element ($ gantt-page)}))))

(defnc app []
  ($ rr/BrowserRouter
     ($ router)))

(defonce root (rdom/createRoot (gdom/getElement "root")))

(defn ^:dev/after-load init! []
  (.render root ($ app)))

(init!)