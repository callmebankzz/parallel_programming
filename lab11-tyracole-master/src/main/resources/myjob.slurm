#!/bin/bash

#SBATCH --job-name=lab11
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=1
#SBATCH --mem=16000m
#SBATCH --time=00:30:00
#SBATCH --mail-user=tjc6@rice.edu
#SBATCH --mail-type=ALL
#SBATCH --export=ALL
#SBATCH --partition=scavenge
#SBATCH --exclusive
 
echo "My job ran from:"
pwd 

echo SLURM_NODELIST=$SLURM_NODELIST
echo USER=$USER

export LAB11_PATH=$HOME/lab11-tyracole

if [[ ! -d $LAB11_PATH ]]; then
    echo "Expected to find a directory containing lab 11 at $LAB11_PATH, but it doesn't seem to exist."
    exit 1
fi

cd $LAB11_PATH

source /home/mjoyner/comp322/322_setup.sh
java -version
mvn --version

mvn clean compile test
