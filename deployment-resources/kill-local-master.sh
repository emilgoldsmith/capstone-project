#!/bin/bash

jps | grep Master | awk '{print $1}' | xargs kill
