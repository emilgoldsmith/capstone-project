#!/bin/bash

ssh ego225@10.230.12.41 -p 4410 'bash -ic "./capstone-project/worker 10.230.12.40:9092 worker1 &> /dev/null"' &> /dev/null &
ssh ego225@10.230.12.42 -p 4410 'bash -ic "./capstone-project/worker 10.230.12.40:9092 worker2 &> /dev/null"' &> /dev/null &
ssh ego225@10.230.12.43 -p 4410 'bash -ic "./capstone-project/worker 10.230.12.40:9092 worker3 &> /dev/null"' &> /dev/null &
ssh ego225@10.230.12.44 -p 4410 'bash -ic "./capstone-project/worker 10.230.12.40:9092 worker4 &> /dev/null"' &> /dev/null &
