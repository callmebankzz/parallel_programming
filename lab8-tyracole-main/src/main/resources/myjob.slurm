#!/bin/bash

#SBATCH --job-name=comp322-lab8
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=1
#SBATCH --cpus-per-task=16
#SBATCH --mem=16000m
#SBATCH --time=00:05:00
#SBATCH --mail-user=tjc6@rice.edu # TODO: Input your netID
#SBATCH --mail-type=ALL
#SBATCH --export=ALL
#SBATCH --partition=commons
#SBATCH --exclusive

echo SLURM_NODELIST=$SLURM_NODELIST
echo USER=$USER

if [[ -d /home/$USER ]]
then
    cd /home/$USER/lab8-tyracole # TODO: Change this to your github lab 8 folder

    source /home/jmg3/comp322/322_setup.sh

    java -version
    mvn --version

    mvn clean
    mvn -Dtest=SpanningTreePerformanceTest test
fi
