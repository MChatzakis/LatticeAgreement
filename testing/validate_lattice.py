import os.path

num_procs = 30

decisions = [[] for i in range(num_procs)]
configs = [[] for i in range(num_procs)]

for i in range(num_procs):
    terminated = False
    empty_stdout = False
    
    if i < 9:
        filename = "./outputs/proc0" + str(i + 1)
    if i >= 9:
        filename = "./outputs/proc" + str(i + 1)

    if os.path.getsize(filename + ".stderr") != 0:
        print(">> Not empty error file:", filename, "for process", i + 1)

    with open(filename + ".config") as f:
        configs.append([])
        f.readline()
        for line in f.readlines():
            configs[i].append(set([int(x) for x in line.strip().split(" ")]))

    if os.path.getsize(filename + ".stdout") == 0:
        empty_stdout = True
        terminated = True
    else:
        with open(filename + ".stdout") as file:
            content = file.read()
            text = "Immediately stopping network packet processing."
            if text in content:
                terminated = True

    if os.path.exists(filename + ".output") == False:
        decisions.append([])
        print(">> File does not exist:", filename, "for process", i + 1)
    else:
        with open(filename + ".output") as f:
            decisions.append([])
            for line in f.readlines():
                decisions[i].append(set([int(x) for x in line.strip().split(" ")]))

    if terminated == False:
                print("Process", i + 1, "had", len(decisions[i]), "decisions (running)")
    else :
        if empty_stdout == True:
            print("Process", i + 1, "had", len(decisions[i]), "decisions (empty stdout)")
        else :
            print("Process", i + 1, "had", len(decisions[i]), "decisions (terminated)")        




mindecided = min([len(decisions[i]) for i in range(num_procs)])


for decision_idx in range(mindecided):
    for d1 in range(num_procs):
        for d2 in range(num_procs):
            ok = decisions[d1][decision_idx].issubset(
                decisions[d2][decision_idx]
            ) or decisions[d2][decision_idx].issubset(decisions[d1][decision_idx])
            if not ok:
                print("p1 error in decision ", decision_idx)
                print("d1=", d1 + 1)
                print("d2=", d2 + 1)
                print("d1: ", decisions[d1][decision_idx])
                print("d2: ", decisions[d2][decision_idx])
                exit()
print("Property 1: SATISFIED")


for proc_id in range(num_procs):
    for i in range(len(decisions[proc_id])):
        ok = configs[proc_id][i].issubset(decisions[proc_id][i])
        if not ok:
            print("p2 error in decision,", i)
            print("decision:", decisions[proc_id][i])
            print("proposal:", configs[proc_id][i])
            exit()
print("Property 2: SATISFIED")
