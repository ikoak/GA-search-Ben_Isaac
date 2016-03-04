(ns simple-search.crossover-experiment
  (:require [simple-search.experiment :as experiment]
        [simple-search.core :as core]
        simple-search.knapsack-examples.knapPI_11_20_1000
        simple-search.knapsack-examples.knapPI_13_20_1000
        simple-search.knapsack-examples.knapPI_16_20_1000
        simple-search.knapsack-examples.knapPI_11_200_1000
        simple-search.knapsack-examples.knapPI_13_200_1000
        simple-search.knapsack-examples.knapPI_16_200_1000
        simple-search.knapsack-examples.knapPI_11_1000_1000
        simple-search.knapsack-examples.knapPI_13_1000_1000
        simple-search.knapsack-examples.knapPI_16_1000_1000))

(defn -main
  "Runs a set of experiments similar to Nic's that should give us some decent
  data on what we've done so far."
  [repetitions tries]
  (ns simple-search.experiment)
  (print-experimental-results
    (run-experiment [
      (with-meta
        (partial core/uniform-crossover core/penalized-score core/normal-crossover 50)
        {:label "uniform_crossover"})
      (with-meta
        (partial core/uniform-crossover-tweak core/penalized-score core/normal-crossover core/mutate-choices 50)
        {:label "uniform_crossover_tweak"})
      (with-meta
        (partial core/uniform-crossover core/penalized-score core/two-point-crossover 50)
        {:label "two_point_uniform_crossover"})
      (with-meta
        (partial core/uniform-crossover-tweak core/penalized-score core/normal-crossover core/mutate-choices 50)
        {:label "two_point_uniform_crossover_tweak"})
      (with-meta
        (partial core/random-search core/penalized-score)
        {:label "basic_mutation"})]
      (map experiment/get-labelled-problem [
        "knapPI_11_20_1000_4" "knapPI_13_20_1000_4" "knapPI_16_20_1000_4"
        "knapPI_11_200_1000_4" "knapPI_13_200_1000_4" "knapPI_16_200_1000_4" "knapPI_16_1000_1000_4"])
      (Integer/parseInt repetitions)
      (Integer/parseInt tries)))
   (shutdown-agents))


(-main "1" "100")


