# Graph DB Benchmark
This is a study of performance comparison for various graph databases.

## Tested databases:
* [Janusgraph](https://github.com/JanusGraph/janusgraph)  V: [0.2.0](https://github.com/JanusGraph/janusgraph/releases)
    * Cassandra backend [^1]
    * Elasticsearch indexing [^2]
* [OrientDB](https://github.com/orientechnologies/orientdb) V: 3.0.0RC1

[^1]: Kubernetes deployments based on [BM/Scalable-Cassandra-deployment-on-Kubernetes](https://github.com/IBM/Scalable-Cassandra-deployment-on-Kubernetes)

[^2]: Kubernetes deployments based on [pires/kubernetes-elasticsearch-cluster](https://github.com/pires/kubernetes-elasticsearch-cluster)

## Test Data
[Amazon product review data](http://jmcauley.ucsd.edu/data/amazon/) is used in this study. Specifically: [5 core Kindle Store](http://snap.stanford.edu/data/amazon/productGraph/categoryFiles/reviews_Kindle_Store_5.json.gz)[^3]

[^3]: Ups and downs: Modeling the visual evolution of fashion trends with one-class collaborative filtering
      R. He, J. McAuley
      WWW, 2016

## Test System
### Hardware
* HP Z620 Workstation
    * 2 x Xeon E5-2620 6 Cores 2.0 GHz
    * 2 x HP 1TB SATA 7200 RPM
    * 32GB RAM (3 x HP 8GB DDR3-1600 + 4 x HP 2GB DDR3-1600)
### Software
* Linux debian 3.16.0-4-amd64 #1 SMP Debian 3.16.51-3 (2017-12-13) x86_64 GNU/Linux
* minikube version: v0.24.1 (run with --vm-driver=none)
* Kubernetes
    * Client Version: version.Info{Major:"1", Minor:"9", GitVersion:"v1.9.2", GitCommit:"5fa2db2bd46ac79e5e00a4e6ed24191080aa463b", GitTreeState:"clean", BuildDate:"2018-01-18T10:09:24Z", GoVersion:"go1.9.2", Compiler:"gc", Platform:"linux/amd64"}
    * Server Version: version.Info{Major:"1", Minor:"8", GitVersion:"v1.8.0", GitCommit:"0b9efaeb34a2fc51ff8e4d34ad9bc6375459c4a4", GitTreeState:"clean", BuildDate:"2017-11-29T22:43:34Z", GoVersion:"go1.9.1", Compiler:"gc", Platform:"linux/amd64"}
* Docker Client
    * Version:      17.05.0-ce
    * API version:  1.29
    * Go version:   go1.7.5
    * Git commit:   89658be
    * Built:        Thu May  4 22:04:27 2017
* Docker Server
    * Version:      17.05.0-ce
    * API version:  1.29 (minimum version 1.12)
    * Go version:   go1.7.5
    * Git commit:   89658be
    * Built:        Thu May  4 22:04:27 2017
    * OS/Arch:      linux/amd64
    * Experimental: false

## Progress
I will share my findings here as I discover them. Any contribution is welcome :)

