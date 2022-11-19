(ns pbranes.helix.app
  (:require [goog.dom :as gdom]
            [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]
            ["react-router-dom" :as rr]))

(defnc layout []
  (d/div {:class "wrapper"}
         (d/header {:class "header"} 
                   (d/nav {:class "nav"}
                    ($ rr/Link {:to "/"} "Home")
                    ($ rr/Link {:to "/1"} "Page 1")))
         (d/div {:class "main"} ($ rr/Outlet))
         (d/footer {:class "footer"} "footer")))

(defnc home []
  (d/div "Home Page"))

(defnc page-1 []
  (d/div "Page One"))

(defnc router []
  ($ rr/Routes
     ($ rr/Route {:path "/" :element ($ layout)}
        ($ rr/Route {:path "/" :element ($ home)})
        ($ rr/Route {:path "/1" :element ($ page-1)}))))

(defnc app []
  ($ rr/BrowserRouter
     ($ router)))

(defonce root (rdom/createRoot (gdom/getElement "root")))

(defn ^:dev/after-load init! []
  (.render root ($ app)))

(init!)