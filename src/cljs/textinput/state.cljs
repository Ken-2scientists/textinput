(ns textinput.state
    (:require [reagent.core :as reagent :refer [atom]]))

(defonce app-state*
  (reagent/atom {"box1" {:comment "Click on any comment (in orange) to edit"}
                 "box2" {:comment "Blank comments start as editable (see below)"}
                 "box3" {}}))
