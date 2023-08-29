#!/bin/bash

#SBATCH --job-name=comp322-lab10
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=1
#SBATCH --cpus-per-task=16
#SBATCH --mem=16000m
#SBATCH --time=00:05:00
#SBATCH --mail-user=NETID@rice.edu # TODO: Input your netID
#SBATCH --mail-type=ALL
#SBATCH --export=ALL
#SBATCH --partition=scavenge
#SBATCH --exclusive

cd /home/$USER/lab10-GITID # TODO: Path to your github lab 10 folder

source  /home/mjoyner/comp322/322_setup.sh

mvn -Dtest=Lab10ActorPerformanceTest clean compile test
