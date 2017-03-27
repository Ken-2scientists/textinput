(ns textinput.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [textinput.state :as state]
              [textinput.atoms :as atm]
              [textinput.props-broken :as prp1]
              [textinput.props-fixed :as prp2]))

(defn show-global-state []
  [:div
   [:h4 "Global state atom"]
   [:pre (with-out-str (cljs.pprint/pprint @state/app-state*))]])

(defn current-page []
  [:div
   [:ul
    [:li [:a {:href "/"} "Passing cursors"]]
    [:li [:a {:href "/props1"} "Passing props, broken re-render"]]
    [:li [:a {:href "/props2"} "Passing props, re-render works"]]]
   [(session/get :current-page) state/app-state*]
   [show-global-state]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'atm/atoms-page))

(secretary/defroute "/props1" []
  (session/put! :current-page #'prp1/props-page))

(secretary/defroute "/props2" []
  (session/put! :current-page #'prp2/props-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
