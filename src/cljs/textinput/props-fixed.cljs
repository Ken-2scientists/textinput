(ns textinput.props-fixed
    (:require [reagent.core :as reagent :refer [atom]]
              [textinput.state :as state]))

(defn text-edit [editing* box-name box-data]
  (fn [editing* box-name box-data]
    [:div
     [:form
      [:textarea {:style {:width "90%"}
                  :default-value (:comment box-data)
                  :on-blur (fn [event]
                             (let [new-text (-> event .-target .-value)]
                               (swap! state/app-state* assoc-in [box-name :comment] new-text)
                               (swap! editing* not)))}]]]))

(defn text-show [editing* box-name box-data]
  [:div
   [:div {:style {:color :orange}
          :on-click (fn [event]
                      (swap! editing* not))}
    (:comment box-data)]
   [:button {:on-click (fn [event]
                         (swap! state/app-state* update box-name dissoc :comment)
                         (swap! editing* not))}
    "Delete comment"]])

(defn text-feedback [box]
  (let [editing* (reagent/atom (clojure.string/blank? (-> box val :comment)))]
    (fn [box]
      (let [[box-name box-data] box
            comment (:comment box-data)]
        [:div {:style {:border "1px solid grey"
                       :margin-bottom "20px"}}
         (if @editing*
           [text-edit editing* box-name box-data]
           [text-show editing* box-name box-data])
         [:pre (str box-data)]]))))

(defn props-page [boxes*]
  [:div
   [:h1 "Props, re-render works"]
   (map-indexed (fn [i x] ^{:key i} [text-feedback x]) @boxes*)])
