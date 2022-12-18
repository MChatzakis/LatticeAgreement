from collections import defaultdict
from functools import reduce
from itertools import product

n_processes = 40
n_decisions = 200

bad_processes = [12, 17, 20, 31]
processes = [p for p in range(1, n_processes + 1) if p not in bad_processes]
pairs_of_processes = product(processes, processes)
decision_seqs = list(range(0, n_decisions))

proposals = defaultdict(lambda: defaultdict(set))
decisions = defaultdict(lambda: defaultdict(set))
# proposals[decision_i][proc_j] = {bli bli bpa}

# Collect results
for i in processes:
    with open(f"./outputs/proc{i:02d}.config") as f:
        # ignore first line
        f.readline()

        for j in range(0, n_decisions):
            line = f.readline()[:-1]
            proposals[j][i] = {int(x) for x in line.split(" ") if x}

    with open(f"proc{i:02d}.output") as f:
        for j in range(0, n_decisions):
            line = f.readline()[:-1]
            decisions[j][i] = {int(x) for x in line.split(" ") if x}

# Check conditions
validity1 = dict()
validity2 = dict()
consistency = dict()

for i in decision_seqs:
    union_of_proposals = reduce(
        lambda a, b: a.union(b), 
        [proposals[i][j] for j in processes]
    )

    validity1[i] = [
        proposals[i][j].issubset(decisions[i][j]) 
        for j 
        in processes
    ]

    validity2[i] = [
        decisions[i][j].issubset(union_of_proposals)
        for j 
        in processes
    ]

    consistency[i] = [
        (
            decisions[i][p_i].issubset(decisions[i][p_j])
            or decisions[i][p_i].issuperset(decisions[i][p_j])
        )
        for p_i, p_j
        in pairs_of_processes
    ]

    print(f"Decision {i}:", all(validity1[i]), all(validity2[i]), all(consistency[i]))

print("All decision:", all(validity1.values()), all(validity2.values()), all(consistency.values()))
