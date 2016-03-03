# Population Search
# Isaac and Ben

# Uniform Crossover

For our uniform crossover, we implemented a basic "normal-crossover" function that uses interleave and partition to combine the two sets of choices. This produces a truely random crossover while keeping some of the basic order to allow for improvements across generations.

The uniform-crossover function takes a scorer (we used penalized-scorer for all of our scoring needs), crossover-type, num-parents, instance, and max-tries. It starts by making all of the parents, and then running the generations equal to the max-tries minus the number of parents. Each generation takes 4 parents at random, crosses them in two sets of two, and picks the best overall result.

# Two-Point Crossover

Our two point crossover uses concat to piece together two sets of choices at random places, in line with the two point crossover suggestions from the text.

# Mutation

We chose to use the basic mutation, mutate-choices. This worked well becase we could apply it directly to the sets of choices before they were added to an instance of an answer, and we ran out of time with the debugging.


