(ns textinput.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(defonce app-state* (reagent/atom {:user-recommendations
                               {"user1" {"type1" {:comment "Click on any comment (in orange) to edit"
                                                  :recs [1 2 3 4]
                                                  :relevant false}
                                         "type2" {:comment "Blank comments start as editable"
                                                  :recs [5 6 7 8]
                                                  :relevant true}
                                         "type3" {:recs [21 21 23]
                                                  :relevant false}}
                                "user2" {"type1" {:comment "Meh"
                                                  :recs [9 10 11 12]}
                                         "type2" {:comment "Great!"
                                                  :recs [13 14 15 16]
                                                  :relevant true}}}}))

(def user1-recommendations
  (reagent/cursor app-state* [:user-recommendations "user1"]))

(defn show-global-state []
  [:div
   [:h4 "Global state atom"]
   [:pre (with-out-str (cljs.pprint/pprint @app-state*))]])

(defn text-edit-cursors [editing* rec-stream]
  [:div
   [:form
    [:textarea {:default-value (:comment @rec-stream)
                :on-blur (fn [event]
                           (let [new-text (-> event .-target .-value)]
                             (swap! rec-stream assoc :comment new-text)
                             (swap! editing* not)))}]]])

(defn text-show-cursors [editing* rec-stream]
  [:div
   [:div {:style {:color :orange}
          :on-click (fn [event]
                      (swap! editing* not))}
    (:comment @rec-stream)]
   [:button {:on-click (fn [event]
                         (swap! rec-stream assoc :comment nil)
                         (swap! editing* not))}
    "Delete comment"]])

(defn text-feedback-cursors [rec-stream]
  (let [comment (:comment @rec-stream)
        editing* (reagent/atom (clojure.string/blank? comment))]
    (fn [rec-stream]
      [:div
       (if @editing*
         [text-edit-cursors editing* rec-stream]
         [text-show-cursors editing* rec-stream])
       [:pre (str @rec-stream)]
       [:hr]])))

(defn cursors-page []
  (let [stream-ids (keys @user1-recommendations)]
    [:div
     [:h1 "Passing cursors"]
     (map-indexed
      (fn [i x]
        ^{:key i}[text-feedback-cursors (reagent/cursor user1-recommendations [x])])
      stream-ids)
     [show-global-state]]))


(defn text-edit-props [editing* stream-name stream-data]
  (fn [editing* stream-name stream-data]
    [:div
     [:form
      [:textarea {:default-value (:comment stream-data)
                  :on-blur (fn [event]
                             (let [new-text (-> event .-target .-value)]
                               (swap! user1-recommendations assoc-in [stream-name :comment] new-text)
                               (swap! editing* not)))}]]]))

(defn text-show-props [editing* stream-name stream-data]
  [:div
   [:div {:style {:color :orange}
          :on-click (fn [event]
                      (swap! editing* not))}
    (:comment stream-data)]
   [:button {:on-click (fn [event]
                         (swap! user1-recommendations assoc-in [stream-name :comment] nil)
                         (swap! editing* not))}
    "Delete comment"]])

(defn text-feedback-props [rec-stream]
  (let [[stream-name stream-data] rec-stream
        comment (:comment stream-data)
        editing* (reagent/atom (clojure.string/blank? comment))]
    (fn [rec-stream]
      [:div
       (if @editing*
         [text-edit-props editing* stream-name stream-data]
         [text-show-props editing* stream-name stream-data])
       [:pre (str stream-data)]
       [:hr]])))



(defn props-page []
  [:div
   [:h1 "Passing props"]
   (map-indexed
    (fn [i x] ^{:key i} [text-feedback-props x])
    @user1-recommendations)
   [show-global-state]])

(defn current-page []
  [:div
   [:ul
    [:li [:a {:href "/"} "Passing cursors"]]
    [:li [:a {:href "/props"} "Passing props"]]]
   [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'cursors-page))

(secretary/defroute "/props" []
  (session/put! :current-page #'props-page))

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
