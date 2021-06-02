# Per a poder visualitzar les grafiques, s'ha d'instalar:
# matplotlib amb pip install matplotlib
# json
# numpy

import json

if __name__ == '__main__':
    try:
        import matplotlib.pyplot as plt
        import numpy as np
    except ImportError:
        print("No es pot executar aquesta opció! S'ha d'instal·lar prèviament la llibreria matplotlib")
        exit(-1)

    with open("../main/Output/EvaluationResults.json", 'r') as infile:
        results = json.load(infile)
    y_mat = []
    for r in results:
        CPU = True
        labels = []
        y = []
        for data in results[r]:
            labels.append(data["title"])
            if CPU:
                y.append(data["elapsedTime"])
            else:
                y.append(data["bytesUsed"])
        y_mat.append(y)

    x = np.arange(len(labels))
    fig, ax = plt.subplots()
    width = 0.35

    i = -1

    for y in y_mat:
        print(y)
        rects1 = ax.bar(x + width * i / 2, y, width / 2, label=list(results.keys())[i + 1])
        i += 1

    ax.set_xticks(x)
    ax.set_xticklabels(labels)

    plt.ylabel("Time (s)")

    ax.ticklabel_format(axis='y', scilimits=[-3, 3])
    plt.title("CPU Benchmark")

    for tick in ax.xaxis.get_major_ticks()[1::2]:
        tick.set_pad(20)

    plt.legend()

    plt.tight_layout()
    plt.show()

    # Boxplots
    for r in results:
        i = 0
        allvalues = []
        for data in results[r]:
            allvalues.append([*data["times"]])

        data = allvalues
        ax = plt.subplot()
        ax.title.set_text( r + " boxplot")
        pos_boxplot = range(0, len(data) * 2, 2)
        plt.boxplot(data, positions=np.array(pos_boxplot), sym='')
        ax.set_ylabel("Time (ns)")

        ax.set_xticks(pos_boxplot)
        ax.set_xticklabels(labels)

        ax.ticklabel_format(axis='y', scilimits=[-3, 3])

        for tick in ax.xaxis.get_major_ticks()[1::2]:
            tick.set_pad(20)

        plt.tight_layout()
        plt.show()