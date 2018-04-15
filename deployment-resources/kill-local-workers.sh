#!/bin/bash

jps | grep Worker | awk '{print $1}' | xargs kill
