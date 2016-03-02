(ns simple-search.core
  (:use simple-search.knapsack-examples.knapPI_11_20_1000
        simple-search.knapsack-examples.knapPI_13_20_1000
        simple-search.knapsack-examples.knapPI_16_20_1000
        simple-search.knapsack-examples.knapPI_16_200_1000))

;;; An answer will be a map with (at least) four entries:
;;;   * :instance
;;;   * :choices - a vector of 0's and 1's indicating whether
;;;        the corresponding item should be included
;;;   * :total-weight - the weight of the chosen items
;;;   * :total-value - the value of the chosen items

(defrecord Answer
  [instance choices total-weight total-value])

(defn included-items
  "Takes a sequences of items and a sequence of choices and
  returns the subsequence of items corresponding to the 1's
  in the choices sequence."
  [items choices]
  (map first
       (filter #(= 1 (second %))
               (map vector items choices))))

(defn make-answer
  [instance choices]
  (let [included (included-items (:items instance) choices)]
    (->Answer instance choices
              (reduce + (map :weight included))
              (reduce + (map :value included)))))

(defn random-answer
  "Construct a random answer for the given instance of the
  knapsack problem."
  [instance]
  (let [choices (repeatedly (count (:items instance))
                            #(rand-int 2))]
    (make-answer instance choices)))

; (random-answer knapPI_13_20_1000_7)

;;; It might be cool to write a function that
;;; generates weighted proportions of 0's and 1's.

(defn score
  "Takes the total-weight of the given answer unless it's over capacity,
   in which case we return 0."
  [answer]
  (if (> (:total-weight answer)
         (:capacity (:instance answer)))
    0
    (:total-value answer)))

(defn penalized-score
  "Takes the total-weight of the given answer unless it's over capacity,
   in which case we return the negative of the total weight."
  [answer]
  (if (> (:total-weight answer)
         (:capacity (:instance answer)))
    (- (:total-weight answer))
    (:total-value answer)))

(defn lexi-score
  [answer]
  (let [shuffled-items (shuffle (included-items (:items (:instance answer))
                                                (:choices answer)))
        capacity (:capacity (:instance answer))]
    (loop [value 0
           weight 0
           items shuffled-items]
      (if (empty? items)
        value
        (let [item (first items)
              w (:weight item)
              v (:value item)]
          (if (> (+ weight w) capacity)
            (recur value weight (rest items))
            (recur (+ value v)
                   (+ weight w)
                   (rest items))))))))

; (lexi-score (random-answer knapPI_16_200_1000_1))

(defn add-score
  "Computes the score of an answer and inserts a new :score field
   to the given answer, returning the augmented answer."
  [scorer answer]
  (assoc answer :score (scorer answer)))

(defn random-search
  [scorer instance max-tries]
  (apply max-key :score
         (map (partial add-score scorer)
              (repeatedly max-tries #(random-answer instance)))))

; (random-search penalized-score knapPI_16_200_1000_1 10000)

(defn mutate-choices
  [choices]
  (let [mutation-rate (/ 1 (count choices))]
    (map #(if (< (rand) mutation-rate) (- 1 %) %) choices)))

(defn mutate-answer
  [answer]
  (make-answer (:instance answer)
               (mutate-choices (:choices answer))))

; (def ra (random-answer knapPI_11_20_1000_1))
; (mutate-answer ra)


; (time (random-search score knapPI_16_200_1000_1 100000
; ))

; (time (hill-climber mutate-answer score knapPI_16_200_1000_1 100000
; ))

; (time (hill-climber mutate-answer penalized-score knapPI_16_200_1000_1 100000
; ))

"___________________________________________###############_______________________________________"
"########################################### OUR FUNCTIONS #######################################"
"*******************************************###############***************************************"


;; best: takes a parent and a child and returns the best of the two
(defn get-best
  [parent child]
  (let [parent-score (:score parent)
        child-score (:score child)]
  (if (< parent-score child-score) child
       parent)
))


;get-scores: takes answer and returns the capacity, total-weight, total-value, and score fields
(defn get-scores
  [answer]
  (let [capacity (:capacity (:instance answer))]
  (merge {:capacity capacity} (select-keys answer [:total-weight :total-value :score]))))

(defn flip-choices [binary times]
    (loop [bin binary x times]
      (if (zero? x) (into () bin)
        (recur (assoc (vec bin) (rand-int (count bin)) (rand-int 2)) (dec x))))
)

(defn make-parents [instance num-parents scorer]
  "takes an instance and the number of parents to be generated"
  (map #(add-score scorer %) (repeatedly num-parents #(random-answer instance))))

(defn normal-crossover [p1 p2]
    "takes two parents and performs uniform crossover on the list of choices"
    (flatten (map rand-nth
         (partition 2 (interleave (:choices p1) (:choices p2))))))


;;need to finish implementing two-point-crossover
(defn two-point-crossover [p1 p2]
    "takes two parents and performs two point crossover on the list of choices"
    (let [size (count p1)
          first-spot (rand (/ size 2))
          second-spot (+ size (rand (/ (count p2))))])
    (flatten (map rand-nth
         (partition 2 (interleave (:choices p1) (:choices p2))))))

(defn uniform-crossover
  [scorer instance max-tries num-parents crossover-type]
  (let [parent-list (make-parents instance num-parents scorer)]
      (apply max-key :score (repeatedly (- max-tries num-parents)
        #(get-best
          (add-score scorer (make-answer instance (crossover-type (rand-nth parent-list) (rand-nth parent-list))))
          (add-score scorer (make-answer instance (crossover-type (rand-nth parent-list) (rand-nth parent-list)))))))))

(get-scores (uniform-crossover penalized-score knapPI_11_20_1000_2 10000 100 two-point-crossover))

(defn uniform-crossover-tweak
  [scorer instance max-tries num-parents tweak crossover-type]
  (let [parent-list (make-parents instance num-parents scorer)]
      (apply max-key :score (repeatedly (- max-tries num-parents)
        #(get-best
          (add-score scorer (make-answer instance (tweak (crossover-type (rand-nth parent-list) (rand-nth parent-list)))))
          (add-score scorer (make-answer instance (tweak (crossover-type (rand-nth parent-list) (rand-nth parent-list))))))))))

(get-scores (uniform-crossover-tweak penalized-score knapPI_11_20_1000_2 10000 100 mutate-choices crossover))





;======random test stuff=====

;=====end random stuff=======



;############### 20 items ###########################

;; ;; Random-Search
(get-scores (random-search penalized-score knapPI_11_20_1000_1 100000))
(get-scores (random-search penalized-score knapPI_13_20_1000_1 100000))
(get-scores (random-search penalized-score knapPI_16_20_1000_1 100000))

;; ;; Uniform Crossover
;(get-scores (uniform-crossover penalized-score knapPI_11_20_1000_1 10))
;(get-scores (uniform-crossover penalized-score knapPI_13_20_1000_1 100000))
;(get-scores (uniform-crossover penalized-score knapPI_16_20_1000_1 100000))

