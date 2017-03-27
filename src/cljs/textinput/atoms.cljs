(ns textinput.atoms
    (:require [reagent.core :as reagent :refer [atom]]))


(defn text-edit [editing* box*]
  [:div
   [:form
    [:textarea {:style {:width "90%"}
                :default-value (:comment @box*)
                :on-blur (fn [event]
                           (let [new-text (-> event .-target .-value)]
                             (swap! box* assoc :comment new-text)
                             (swap! editing* not)))}]]])

(defn text-show [editing* box*]
  [:div
   [:div {:style {:color :orange}
          :on-click (fn [event]
                      (swap! editing* not))}
    (:comment @box*)]
   [:button {:on-click (fn [event]
                         (swap! box* dissoc :comment)
                         (swap! editing* not))}
    "Delete comment"]])

(defn text-feedback [box*]
  (let [comment (:comment @box*)
        editing* (reagent/atom (clojure.string/blank? comment))]
    (fn [box*]
      [:div {:style {:border "1px solid grey"
                     :margin-bottom "20px"}}
       (if @editing*
         [text-edit editing* box*]
         [text-show editing* box*])
       [:pre (str @box*)]])))

(defn atoms-page [boxes*]
  (let [box-ids (keys @boxes*)]
    [:div
     [:h1 "Passing cursors"]
     (map-indexed
      (fn [i x]
        ^{:key i}[text-feedback (reagent/cursor boxes* [x])])
      box-ids)]))
