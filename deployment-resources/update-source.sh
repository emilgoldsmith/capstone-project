#!/bin/bash

PIDs=()
ssh ego225@10.230.12.40 -p 4410 'bash -ic "cd ~/capstone-project/main && git pull && sbt stage"' &
PIDs+=($!)
ssh ego225@10.230.12.41 -p 4410 'bash -ic "cd ~/capstone-project/main && git pull && sbt stage"' &
PIDs+=($!)
ssh ego225@10.230.12.42 -p 4410 'bash -ic "cd ~/capstone-project/main && git pull && sbt stage"' &
PIDs+=($!)
ssh ego225@10.230.12.43 -p 4410 'bash -ic "cd ~/capstone-project/main && git pull && sbt stage"' &
PIDs+=($!)
ssh ego225@10.230.12.44 -p 4410 'bash -ic "cd ~/capstone-project/main && git pull && sbt stage"' &
PIDs+=($!)

FAIL=0
for job in ${PIDs[@]}
do
  wait $job || let "FAIL+=1"
done

if [[ $FAIL == 0 ]];
then
  echo "Updated source successfully"
else
  echo "There were errors while updating source"
fi
