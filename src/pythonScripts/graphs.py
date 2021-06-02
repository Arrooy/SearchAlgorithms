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

    for r in results:
        CPU = True
        x = []
        y = []
        for data in results[r]:
            x.append(data["title"])
            if CPU:
                y.append(data["elapsedTime"])
            else:
                y.append(data["bytesUsed"])

        plt.bar(x,y, label=r)
        #     plt.loglog(x, y, '-o',label=label)
        ax = plt.gca()

        plt.ylabel("Time (s)")

        ax.ticklabel_format(axis='y', scilimits=[-3, 3])
        plt.title("CPU Benchmark")

        for tick in ax.xaxis.get_major_ticks()[1::2]:
            tick.set_pad(20)

        plt.tight_layout()
        plt.legend()

    plt.show()

    # Boxplots
    for r in results:
        i = 0
        allvalues = []
        for data in results[r]:
            allvalues.append([*data["times"]])

        data = allvalues
        ax = plt.subplot()
        ax.title.set_text( "Boxplot ")
        pos_boxplot = range(0, len(data) * 2, 2)
        plt.boxplot(data, positions=np.array(pos_boxplot), sym='')
        ax.set_ylabel("a")
        ax.set_xlabel("x_label")
        # plt.xticks(pos_boxplot, labels)
        plt.tight_layout()
        plt.show()